package com.example.dadambackend.domain.question.dto.response;

import com.example.dadambackend.domain.question.model.Question;
import com.example.dadambackend.domain.question.model.QuestionCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/**
 * [역할]
 * - 클라이언트에 내려주는 "질문 응답 DTO"입니다.
 * - 엔티티(Question)를 그대로 노출하지 않고, API에 필요한 필드만 선별해서 전달합니다.
 *
 * [Swagger 연동]
 * - @Schema를 사용해서 필드 설명과 예시 값을 추가하면
 *   Swagger UI에서 자동으로 문서화됩니다.
 */
@Getter
@Builder
@Schema(
        name = "QuestionResponse",
        description = "오늘의 질문 정보를 담는 응답 객체"
)
public class QuestionResponse {

    @Schema(description = "질문 ID", example = "1")
    private Long id;

    @Schema(description = "질문 내용", example = "가족과 함께한 가장 즐거웠던 여행은 무엇인가요?")
    private String content;

    @Schema(
            description = "질문 카테고리",
            example = "TRAVEL" // enum 값 예시 (실제 enum 이름에 맞게 수정)
    )
    private QuestionCategory category;

    @Schema(
            description = "이 질문이 '오늘의 질문'으로 배포된 날짜",
            example = "2025-11-13"
    )
    private LocalDate assignedDate;

    /**
     * Question 엔티티 → QuestionResponse DTO 변환 메서드
     *
     * @param question     질문 엔티티
     * @param assignedDate 이 질문이 '오늘의 질문'으로 사용된 날짜
     *                      - 오늘의 질문 API에서는 요청 파라미터로 받은 date를 그대로 넣어줄 수 있습니다.
     */
    public static QuestionResponse of(Question question, LocalDate assignedDate) {
        return QuestionResponse.builder()
                .id(question.getId())
                .content(question.getContent())
                .category(question.getCategory())
                .assignedDate(assignedDate)
                .build();
    }
}
