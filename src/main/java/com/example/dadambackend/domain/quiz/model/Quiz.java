// com/example/dadambackend/domain/quiz/model/Quiz.java
package com.example.dadambackend.domain.quiz.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String questionContent;

    // 4개의 선택지
    @Column(name = "option_a", nullable = false)
    private String optionA;

    @Column(name = "option_b", nullable = false)
    private String optionB;

    @Column(name = "option_c", nullable = false)
    private String optionC;

    @Column(name = "option_d", nullable = false)
    private String optionD;

    // 정답 ('A', 'B', 'C', 'D' 중 하나)
    @Column(nullable = false, length = 1)
    private String correctAnswer;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 생성자 (테스트 및 초기 데이터용)
    public Quiz(String questionContent, String optionA, String optionB, String optionC, String optionD, String correctAnswer, LocalDateTime createdAt) {
        this.questionContent = questionContent;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
        this.createdAt = createdAt;
    }
}
