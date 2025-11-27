// com/example/dadambackend/domain/quiz/service/QuizService.java
package com.example.dadambackend.domain.quiz.service;

import com.example.dadambackend.domain.quiz.dto.QuizResponse;
import com.example.dadambackend.domain.quiz.dto.QuizSelectionInfo;
import com.example.dadambackend.domain.quiz.model.Quiz;
import com.example.dadambackend.domain.quiz.model.QuizSelection;
import com.example.dadambackend.domain.quiz.repository.QuizRepository;
import com.example.dadambackend.domain.quiz.repository.QuizSelectionRepository;
import com.example.dadambackend.domain.user.model.User;
import com.example.dadambackend.domain.user.repository.UserRepository;
import com.example.dadambackend.global.exception.BusinessException;
import com.example.dadambackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizService {
    private final QuizRepository quizRepository;
    private final QuizSelectionRepository selectionRepository;
    private final UserRepository userRepository;

    private final Long TEMP_USER_ID = 1L; // 현재 사용자 ID (임시)

    /**
     * 현재 활성화된 퀴즈를 조회하고, 참여 여부 및 상세 결과를 반환합니다.
     */
    public QuizResponse getCurrentQuizWithStatus() {
        // 1. 가장 최근 퀴즈 조회 (활성화된 퀴즈)
        Quiz quiz = quizRepository.findTopByOrderByCreatedAtDesc()
                .orElseThrow(() -> new BusinessException(ErrorCode.GAME_NOT_FOUND)); // 에러 코드 재사용

        // 2. 현재 사용자의 참여 여부 확인
        boolean hasParticipated = selectionRepository.existsByQuizIdAndUserId(
                quiz.getId(), TEMP_USER_ID);

        // 3. 나의 선택 정보 조회 (정답 유무 확인용)
        Optional<QuizSelection> mySelectionOptional = Optional.empty();
        if (hasParticipated) {
            // (Note: Repository에 findByQuizIdAndUserId 메서드가 필요하지만, 여기서는 findByQuizId 리스트에서 필터링한다고 가정)
            // 실제 구현 시 Repository에 findByQuizIdAndUserId(Long quizId, Long userId)를 추가하는 것이 좋습니다.
            mySelectionOptional = selectionRepository.findByQuizId(quiz.getId()).stream()
                    .filter(s -> s.getUser().getId().equals(TEMP_USER_ID))
                    .findFirst();
        }

        // 4. 모든 참여자의 선택 정보 조회 및 DTO 변환
        List<QuizSelectionInfo> selectionDetails = selectionRepository.findByQuizId(quiz.getId()).stream()
                .map(QuizSelectionInfo::from)
                .collect(Collectors.toList());

        // 5. 응답 DTO 생성
        return QuizResponse.from(quiz, hasParticipated, mySelectionOptional.orElse(null), selectionDetails);
    }

    /**
     * 퀴즈 답변 제출 로직
     */
    @Transactional
    public void submitSelection(Long quizId, String selectedOption) {
        // 1. 중복 참여 확인
        if (selectionRepository.existsByQuizIdAndUserId(quizId, TEMP_USER_ID)) {
            throw new BusinessException(ErrorCode.ALREADY_PARTICIPATED);
        }

        // 2. 퀴즈와 사용자 객체 조회
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GAME_NOT_FOUND));

        User user = userRepository.findById(TEMP_USER_ID)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 3. 답변 저장 (QuizSelection 생성자에서 정답 여부 자동 판단)
        QuizSelection selection = new QuizSelection(quiz, user, selectedOption);
        selectionRepository.save(selection);
    }
}
