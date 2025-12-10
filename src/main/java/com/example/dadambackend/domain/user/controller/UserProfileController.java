package com.example.dadambackend.domain.user.controller;

import com.example.dadambackend.domain.user.dto.response.FamilyCodeResponse;
import com.example.dadambackend.domain.user.dto.response.UserProfileResponse;
import com.example.dadambackend.domain.user.service.UserProfileService;
import com.example.dadambackend.global.exception.BusinessException;
import com.example.dadambackend.global.exception.ErrorCode;
import com.example.dadambackend.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "사용자 프로필", description = "내 프로필 조회 및 수정 API")
@SecurityRequirement(name = "Authorization")   // 🔐 이 컨트롤러의 API들은 JWT 필요
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Authorization 헤더에서 userId 추출
     */
    private Long extractUserIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        System.out.println("[DEBUG] Authorization header = " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);
        try {
            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            System.out.println("[DEBUG] Parsed userId from token = " + userId);
            return userId;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
    }

    @GetMapping("/me")
    @Operation(summary = "내 프로필 조회", description = "현재 로그인한 사용자의 프로필 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = UserProfileResponse.class)))
    public ResponseEntity<UserProfileResponse> getMyProfile(HttpServletRequest request) {
        Long userId = extractUserIdFromRequest(request);
        return ResponseEntity.ok(userProfileService.getProfile(userId));
    }

    @PostMapping(
            value = "/me",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(summary = "내 프로필 수정", description = "이름, 가족 역할, 가족 코드, 프로필 이미지를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(schema = @Schema(implementation = UserProfileResponse.class)))
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            HttpServletRequest request,
            @RequestPart(required = false) String name,
            @RequestPart(required = false) String familyRole,   // child / parent / grandparent
            @RequestPart(required = false) String familyCode,
            @RequestPart(required = false) MultipartFile avatar
    ) {
        Long userId = extractUserIdFromRequest(request);
        UserProfileResponse response = userProfileService.updateProfile(
                userId,
                name,
                familyRole,
                familyCode,
                avatar
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/me/avatar")
    @Operation(summary = "프로필 이미지 삭제", description = "프로필 이미지를 삭제하고 기본 아바타로 되돌립니다.")
    public ResponseEntity<UserProfileResponse> resetAvatar(HttpServletRequest request) {
        Long userId = extractUserIdFromRequest(request);
        UserProfileResponse response = userProfileService.deleteAvatar(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/family")
    @Operation(summary = "내 가족 멤버 조회", description = "같은 familyCode를 가진 가족 멤버 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = UserProfileResponse.class)))
    public ResponseEntity<List<UserProfileResponse>> getMyFamilyMembers(HttpServletRequest request) {
        Long userId = extractUserIdFromRequest(request);
        List<UserProfileResponse> members = userProfileService.getMyFamilyMembers(userId);
        return ResponseEntity.ok(members);
    }

    @PostMapping("/me/family-code")
    @Operation(summary = "가족 코드 생성 또는 조회", description = "내 가족 초대 코드를 생성하거나 이미 있으면 그대로 반환합니다.")
    public ResponseEntity<FamilyCodeResponse> generateOrGetFamilyCode(HttpServletRequest request) {
        Long userId = extractUserIdFromRequest(request);
        String code = userProfileService.generateOrGetFamilyCode(userId); // 아래에서 위임 추가
        return ResponseEntity.ok(new FamilyCodeResponse(code));
    }

}
