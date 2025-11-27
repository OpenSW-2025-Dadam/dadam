// com/example/dadambackend/domain/quiz/model/QuizSelection.java
package com.example.dadambackend.domain.quiz.model;

import com.example.dadambackend.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizSelection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // N:1 관계 - 어떤 퀴즈에 대한 답변인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    // N:1 관계 - 누가 답변했는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 사용자가 선택한 옵션 ('A', 'B', 'C', 'D' 중 하나)
    @Column(name = "selected_option", nullable = false, length = 1)
    private String selectedOption;

    // 정답 여부
    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect;

    private LocalDateTime createdAt;

    // 생성자 (답변 저장용)
    public QuizSelection(Quiz quiz, User user, String selectedOption) {
        this.quiz = quiz;
        this.user = user;
        this.selectedOption = selectedOption;
        // 정답 여부 판단
        this.isCorrect = selectedOption.equals(quiz.getCorrectAnswer());
        this.createdAt = LocalDateTime.now();
    }
}