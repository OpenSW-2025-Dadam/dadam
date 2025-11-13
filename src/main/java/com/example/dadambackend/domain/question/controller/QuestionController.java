package com.example.dadambackend.domain.question.controller;

import com.example.dadambackend.domain.question.dto.response.QuestionResponse;
import com.example.dadambackend.domain.question.model.Question;
import com.example.dadambackend.domain.question.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    /**
     * [API 설명]
     * - 특정 날짜의 "오늘의 질문"을 조회하는 엔드포인트입니다.
     * - 예: GET /api/v1/questions?date=2025-11-13
     *
     * [REST 관점]
     * - 리소스: questions
     * - 행위: GET (조회)
     * - 날짜는 조회 조건이므로 Query Parameter로 전달합니다.
     */
    @Operation(
            summary = "특정 날짜의 오늘의 질문 조회",
            description = """
                    주어진 날짜에 해당하는 '오늘의 질문'을 조회합니다.
                    - 프론트에서 오늘 날짜를 사용하면 '오늘의 질문' API로 동작합니다.
                    - 예: GET /api/v1/questions?date=2025-11-13
                    """
    )
    @GetMapping
    public ResponseEntity<QuestionResponse> getQuestionByDate(
            @Parameter(
                    description = "질문을 조회할 날짜 (yyyy-MM-dd 형식)",
                    example = "2025-11-13",
                    required = true
            )
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // 문자열 → LocalDate 자동 변환
            LocalDate date
    ) {
        // 👉 서비스 레이어에서 해당 날짜의 질문을 찾아옵니다.
        //    QuestionService에 getQuestionByDate(LocalDate date) 메서드가 있다고 가정합니다.
        Question question = questionService.getQuestionByDate(date);

        // 👉 응답 DTO로 매핑해서 반환합니다.
        //    assignedDate에는 실제로 이 질문이 "오늘의 질문"으로 배포된 날짜를 넣습니다.
        return ResponseEntity.ok(QuestionResponse.of(question, date));
    }
}
