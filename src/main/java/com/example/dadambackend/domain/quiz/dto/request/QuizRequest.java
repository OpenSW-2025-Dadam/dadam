// com/example/dadambackend/domain/quiz/dto/QuizRequest.java
package com.example.dadambackend.domain.quiz.dto;

import lombok.Getter;

@Getter
public class QuizRequest {
    private String selectedOption; // 'A', 'B', 'C', 'D' 중 하나
}
