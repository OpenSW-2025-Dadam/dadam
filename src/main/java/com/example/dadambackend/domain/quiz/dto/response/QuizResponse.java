// com/example/dadambackend/domain/quiz/dto/QuizResponse.java
package com.example.dadambackend.domain.quiz.dto;

import com.example.dadambackend.domain.quiz.model.Quiz;
import com.example.dadambackend.domain.quiz.model.QuizSelection;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class QuizResponse {
    private Long id;
    private String questionContent;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    private boolean hasParticipated;

    // ⭐ 결과 공개 필드
    private String correctAnswer;
    private boolean isMyAnswerCorrect; // 나의 정답 유무

    // ⭐ 전체 참여자 상세 정보
    private List<QuizSelectionInfo> selectionDetails;

    // 정답 공개 여부를 결정하는 팩토리 메서드
    public static QuizResponse from(Quiz quiz, boolean participated, QuizSelection mySelection, List<QuizSelectionInfo> selectionDetails) {

        // 참여했다면 정답과 결과를 공개합니다.
        String finalCorrectAnswer = participated ? quiz.getCorrectAnswer() : null;
        boolean finalIsMyAnswerCorrect = participated && mySelection != null ? mySelection.isCorrect() : false;

        return QuizResponse.builder()
                .id(quiz.getId())
                .questionContent(quiz.getQuestionContent())
                .optionA(quiz.getOptionA())
                .optionB(quiz.getOptionB())
                .optionC(quiz.getOptionC())
                .optionD(quiz.getOptionD())
                .hasParticipated(participated)
                .correctAnswer(finalCorrectAnswer)
                .isMyAnswerCorrect(finalIsMyAnswerCorrect)
                .selectionDetails(selectionDetails)
                .build();
    }
}