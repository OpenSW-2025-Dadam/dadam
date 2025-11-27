// com/example/dadambackend/domain/quiz/repository/QuizSelectionRepository.java
package com.example.dadambackend.domain.quiz.repository;

import com.example.dadambackend.domain.quiz.model.QuizSelection;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizSelectionRepository extends JpaRepository<QuizSelection, Long> {
    // 특정 퀴즈에 대한 특정 사용자의 참여 여부 확인
    boolean existsByQuizIdAndUserId(Long quizId, Long userId);

    // 특정 퀴즈에 대한 모든 선택 정보를 조회 (결과 공개용)
    List<QuizSelection> findByQuizId(Long quizId);
}
