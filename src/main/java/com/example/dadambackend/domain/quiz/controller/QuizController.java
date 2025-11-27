// com/example/dadambackend/domain/quiz/controller/QuizController.java
package com.example.dadambackend.domain.quiz.controller;

import com.example.dadambackend.domain.quiz.dto.QuizRequest;
import com.example.dadambackend.domain.quiz.dto.QuizResponse;
import com.example.dadambackend.domain.quiz.service.QuizService;
import com.example.dadambackend.global.exception.BusinessException;
import com.example.dadambackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    /**
     * GET /api/v1/quizzes/current
     * 현재 활성화된 퀴즈 질문과 사용자 참여 상태, 결과를 조회
     */
    @GetMapping("/current")
    public ResponseEntity<QuizResponse> getCurrentQuiz() {
        QuizResponse response = quizService.getCurrentQuizWithStatus();
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/quizzes/{quizId}/select
     * 퀴즈에 답변 제출
     */
    @PostMapping("/{quizId}/select")
    public ResponseEntity<Void> submitSelection(
            @PathVariable Long quizId,
            @RequestBody QuizRequest request) {

        String selectedOption = request.getSelectedOption();

        // 선택지 유효성 검사 (A, B, C, D 중 하나인지)
        if (!selectedOption.matches("[A-D]")) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        quizService.submitSelection(quizId, selectedOption);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
