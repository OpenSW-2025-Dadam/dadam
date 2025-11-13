package com.example.dadambackend.domain.answer.controller;

import com.example.dadambackend.domain.answer.dto.request.CreateAnswerRequest;
import com.example.dadambackend.domain.answer.dto.response.AnswerResponse;
import com.example.dadambackend.domain.answer.service.AnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
@Tag(name = "Answer API", description = "질문에 대한 답변 작성 및 조회 API")
public class AnswerController {

    private final AnswerService answerService;

    // TODO: userId는 실제로는 SecurityContext(JWT)에서 가져와야 합니다.
    //  - 현재는 로그인/인증 기능이 없으므로, 임시로 고정 사용자 ID(1L)를 사용합니다.
    private static final Long TEMP_USER_ID = 1L;

    /**
     * [API 설명]
     *  - 특정 질문에 대해 답변을 작성하는 엔드포인트입니다.
     *
     * [REST 구조]
     *  - 리소스: /api/v1/questions/{questionId}/answers
     *  - 행위: POST (생성)
     *
     * 예)
     *  - POST /api/v1/questions/1/answers
     *  - Body: { "content": "저는 제주도 여행이 가장 즐거웠어요!" }
     */
    @Operation(
            summary = "질문에 대한 답변 작성",
            description = """
                    특정 질문에 대해 현재 사용자(임시: ID=1)가 답변을 작성합니다.
                    - URL의 {questionId}로 어떤 질문인지 지정합니다.
                    - 본문에는 답변 내용을 담은 JSON을 전송합니다.
                    - 한 사용자는 한 질문에 대해 한 번만 답변할 수 있습니다.
                    """
    )
    @PostMapping("/{questionId}/answers")
    public ResponseEntity<AnswerResponse> createAnswer(
            @Parameter(description = "답변을 작성할 질문 ID", example = "1", required = true)
            @PathVariable Long questionId,

            @Valid @RequestBody CreateAnswerRequest request
    ) {
        // 현재는 TEMP_USER_ID(1L)를 사용하지만,
        // 나중에는 SecurityContext에서 인증된 사용자 ID를 가져와야 합니다.
        AnswerResponse response = answerService.createAnswer(questionId, TEMP_USER_ID, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * [API 설명]
     *  - 특정 질문에 달린 모든 답변 목록을 조회하는 엔드포인트입니다.
     *
     * [REST 구조]
     *  - 리소스: /api/v1/questions/{questionId}/answers
     *  - 행위: GET (조회)
     *
     * 예)
     *  - GET /api/v1/questions/1/answers
     */
    @Operation(
            summary = "질문에 대한 답변 목록 조회",
            description = """
                    특정 질문에 대해 작성된 모든 답변을 조회합니다.
                    - URL의 {questionId}로 어떤 질문에 대한 답변 목록인지 지정합니다.
                    - 응답으로 AnswerResponse 리스트를 반환합니다.
                    """
    )
    @GetMapping("/{questionId}/answers")
    public ResponseEntity<List<AnswerResponse>> getAnswers(
            @Parameter(description = "답변 목록을 조회할 질문 ID", example = "1", required = true)
            @PathVariable Long questionId
    ) {
        List<AnswerResponse> responses = answerService.getAnswersByQuestionId(questionId);
        return ResponseEntity.ok(responses);
    }
}
