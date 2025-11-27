// com/example/dadambackend/domain/quiz/repository/QuizRepository.java
package com.example.dadambackend.domain.quiz.repository;

import com.example.dadambackend.domain.quiz.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    // 가장 최근에 생성된 퀴즈 질문을 찾는 메서드 (기간별 활성화 구현)
    Optional<Quiz> findTopByOrderByCreatedAtDesc();
}
