package com.example.dadambackend.domain.question.service;

import com.example.dadambackend.domain.question.model.Question;
import com.example.dadambackend.domain.question.model.QuestionCategory;
import com.example.dadambackend.domain.question.repository.QuestionRepository;
import com.example.dadambackend.global.exception.BusinessException;
import com.example.dadambackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;

    /**
     * [역할]
     *  - 특정 날짜에 해당하는 "오늘의 질문"을 조회하는 서비스 메서드입니다.
     *  - 컨트롤러에서 넘어온 date(yyyy-MM-dd)를 받아서,
     *    해당 날짜에 배포된 질문을 찾아 반환하는 것이 최종 목표입니다.
     *
     * [현재 상태 - 임시 구현]
     *  - 아직 "날짜별 질문 배포(QuestionAssignment, 스케줄러 등)" 기능이 없기 때문에
     *    실제 date 값을 사용하지 않고, 가장 최근에 생성된 질문을 오늘의 질문이라고 가정합니다.
     *
     * [TODO - 나중에 구현해야 할 것]
     *  - QuestionAssignment(또는 비슷한 엔티티)를 도입해서
     *    "어떤 날짜에 어떤 Question이 배포되었는지"를 저장한 뒤,
     *    아래 로직을 예시처럼 변경해야 합니다.
     *
     *    예)
     *      return questionAssignmentRepository.findByAssignedDate(date)
     *              .map(QuestionAssignment::getQuestion)
     *              .orElseThrow(...);
     *
     * @param date 오늘의 질문을 조회할 날짜
     * @return 해당 날짜의 질문 (현재는 '가장 최근 질문'을 임시로 반환)
     */
    public Question getQuestionByDate(LocalDate date) {
        // ⚠️ 임시 구현:
        //  - 날짜별 질문 배포 로직이 없어서,
        //    지금은 단순히 "가장 최근에 생성된 질문"을 오늘의 질문으로 간주합니다.
        return questionRepository.findTopByOrderByCreatedAtDesc()
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));
    }

    /**
     * [역할]
     *  - questionId로 질문을 단건 조회하는 메서드입니다.
     *  - 답변 작성 시, 실제로 존재하는 질문인지 검증할 때 사용합니다.
     *
     * @param questionId 조회할 질문 ID
     * @return 존재하는 질문 엔티티
     * @throws BusinessException 질문이 존재하지 않을 경우 QUESTION_NOT_FOUND 예외 발생
     */
    public Question getQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));
    }

    /**
     * [역할]
     *  - DB가 비어 있을 때, 초기 테스트용 질문을 한 개 넣어주는 메서드입니다.
     *  - 애플리케이션 구동 시점에 호출하면,
     *    최소한 1개의 질문은 존재하도록 보장할 수 있습니다.
     *
     * [사용 예시]
     *  - CommandLineRunner, ApplicationRunner, @PostConstruct 등에서 호출 가능
     *
     *      @Component
     *      @RequiredArgsConstructor
     *      public class DataInitializer implements CommandLineRunner {
     *          private final QuestionService questionService;
     *
     *          @Override
     *          public void run(String... args) {
     *              questionService.createInitialQuestion();
     *          }
     *      }
     */
    @Transactional
    public void createInitialQuestion() {
        // 이미 데이터가 있으면 아무 것도 하지 않습니다.
        if (questionRepository.count() == 0) {
            Question initialQuestion = new Question(
                    "가족과 함께한 가장 즐거웠던 여행은 무엇인가요?",
                    QuestionCategory.TRAVEL
            );
            questionRepository.save(initialQuestion);
        }
    }
}
