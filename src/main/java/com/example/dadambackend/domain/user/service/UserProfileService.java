package com.example.dadambackend.domain.user.service;

import com.example.dadambackend.domain.user.dto.response.UserProfileResponse;
import com.example.dadambackend.domain.user.model.User;
import com.example.dadambackend.global.exception.BusinessException;
import com.example.dadambackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileService {

    private final UserService userService;   // ✅ UserService 주입으로 가족 코드 로직 재사용

    /**
     * 실행 중인 애플리케이션의 현재 작업 디렉터리 기준:
     *   {프로젝트 루트}/uploads/avatars
     */
    private final Path avatarBasePath =
            Paths.get(System.getProperty("user.dir"), "uploads", "avatars");

    private final String AVATAR_URL_PREFIX = "/uploads/avatars/";

    /**
     * 애플리케이션 시작 시 한 번 호출해 주면 디렉터리 미리 만들어짐
     */
    @Transactional
    public void initUploadDir() {
        try {
            Files.createDirectories(avatarBasePath);
            System.out.println("[UserProfileService] avatarBasePath = " + avatarBasePath.toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지 업로드 디렉터리 생성 실패", e);
        }
    }

    /**
     * 내 프로필 조회
     */
    public UserProfileResponse getProfile(Long userId) {
        User user = userService.getById(userId);
        return UserProfileResponse.from(user);
    }

    /**
     * ✅ 내 가족 멤버 조회
     *    - UserService.getFamilyMembers 를 재사용해서, 도메인 로직은 한 곳에만 둔다.
     */
    public List<UserProfileResponse> getMyFamilyMembers(Long userId) {
        List<User> members = userService.getFamilyMembers(userId);
        return members.stream()
                .map(UserProfileResponse::from)
                .toList();
    }

    /**
     * 이름 / 역할 / 가족코드 + (옵션) 아바타까지 한 번에 수정
     * - 가족 코드 검증/적용은 UserService.updateProfile 이 담당
     */
    @Transactional
    public UserProfileResponse updateProfile(Long userId,
                                             String name,
                                             String familyRole,
                                             String familyCode,
                                             MultipartFile avatarFile) {

        // 1) 먼저 현재 유저 엔티티 조회 (파일명 생성, 기존 아바타 삭제 등에 사용)
        User user = userService.getById(userId);

        // 2) 새 아바타 파일이 있으면 저장하고 URL 생성
        String avatarUrl = null;
        if (avatarFile != null && !avatarFile.isEmpty()) {
            avatarUrl = replaceAvatarFile(user, avatarFile);
        }

        // 3) UserService.updateProfile 을 통해
        //    - 이름 / 역할 / 가족 코드 검증 & 적용
        //    - avatarUrl 반영
        User updated = userService.updateProfile(
                userId,
                name,
                familyRole,
                familyCode,
                avatarUrl
        );

        return UserProfileResponse.from(updated);
    }

    /**
     * 프사만 교체
     */
    @Transactional
    public UserProfileResponse updateAvatar(Long userId, MultipartFile avatarFile) {
        if (avatarFile == null || avatarFile.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        User user = userService.getById(userId);
        String avatarUrl = replaceAvatarFile(user, avatarFile);
        user.updateAvatar(avatarUrl);

        return UserProfileResponse.from(user);
    }

    /**
     * 프사 삭제 (기본 아바타로)
     */
    @Transactional
    public UserProfileResponse deleteAvatar(Long userId) {
        User user = userService.getById(userId);

        deleteAvatarFileIfExists(user.getAvatarUrl());
        user.clearAvatar();

        return UserProfileResponse.from(user);
    }

    // ================= 내부 유틸 =================

    /**
     * 기존 파일 삭제 + 새 파일 저장 후 URL 반환
     */
    private String replaceAvatarFile(User user, MultipartFile avatarFile) {
        deleteAvatarFileIfExists(user.getAvatarUrl());
        return saveAvatarFile(user, avatarFile);
    }

    /**
     * 실제 파일 저장
     */
    private String saveAvatarFile(User user, MultipartFile avatarFile) {
        try {
            // 디렉터리 보장
            Files.createDirectories(avatarBasePath);

            String ext = getExtension(avatarFile.getOriginalFilename());
            String newFileName = user.getId() + "_" + System.currentTimeMillis();
            if (ext != null) {
                newFileName += "." + ext;
            }

            Path target = avatarBasePath.resolve(newFileName);

            System.out.println("[UserProfileService] Saving avatar to: " + target.toAbsolutePath());

            avatarFile.transferTo(target.toFile());

            return AVATAR_URL_PREFIX + newFileName;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("프로필 이미지 저장 실패: " + e.getMessage(), e);
        }
    }

    private void deleteAvatarFileIfExists(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.isBlank()) return;
        if (!avatarUrl.startsWith(AVATAR_URL_PREFIX)) return;

        String fileName = avatarUrl.substring(AVATAR_URL_PREFIX.length());
        Path target = avatarBasePath.resolve(fileName);

        try {
            Files.deleteIfExists(target);
            System.out.println("[UserProfileService] Deleted avatar file: " + target.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("[UserProfileService] 프로필 이미지 삭제 실패: " + target.toAbsolutePath()
                    + " / " + e.getMessage());
        }
    }

    private String getExtension(String filename) {
        if (filename == null) return null;
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1) return null;
        return filename.substring(dotIndex + 1);
    }
    public String generateOrGetFamilyCode(Long userId) {
        return userService.generateOrGetFamilyCode(userId);
    }

}
