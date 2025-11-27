// com/example/dadambackend/domain/quiz/dto/QuizSelectionInfo.java
package com.example.dadambackend.domain.quiz.dto;

import com.example.dadambackend.domain.quiz.model.QuizSelection;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizSelectionInfo {
    private Long userId;
    private String userName;
    private String selectedOption; // 선택지
    private boolean isCorrect;      // 정답 여부

    public static QuizSelectionInfo from(QuizSelection selection) {
        // User 엔티티의 name과 id 필드가 있다고 가정합니다.
        return QuizSelectionInfo.builder()
                .userId(selection.getUser().getId())
                .userName(selection.getUser().getName())
                .selectedOption(selection.getSelectedOption())
                .isCorrect(selection.isCorrect())
                .build();
    }
}
