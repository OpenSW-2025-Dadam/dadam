package com.example.dadambackend.domain.answer.service;

import com.example.dadambackend.domain.answer.dto.request.CreateAnswerRequest;
import com.example.dadambackend.domain.answer.dto.response.AnswerResponse;
import com.example.dadambackend.domain.answer.model.Answer;
import com.example.dadambackend.domain.answer.repository.AnswerRepository;
import com.example.dadambackend.domain.question.model.Question;
import com.example.dadambackend.domain.question.service.QuestionService;
import com.example.dadambackend.domain.user.model.User;
import com.example.dadambackend.domain.user.repository.UserRepository; // 임시 사용
import com.example.dadambackend.global.exception.BusinessException;
import com.example.dadambackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionService questionService;
    private final UserRepository userRepository; // TODO: 실제 구현 시 SecurityContext 기반으로 변경 예정

    /**
     * [역할]
     *  - 특정 질문(questionId)에 대해, 특정 사용자(userId)가 답변을 작성하는 서비스 메서드입니다.
     *
     * [검증 흐름]
     *  1) questionId로 질문이 실제 존재하는지 확인
     *  2) userId로 사용자가 실제 존재하는지 확인
     *  3) 해당 질문에 이미 답변을 작성한 적이 있는지 검사 (중복 답변 방지)
     *  4) 위 검증을 모두 통과하면 Answer 엔티티 생성 후 저장
     *
     * @param questionId 질문 ID (URL path에서 넘어옴)
     * @param userId     답변 작성자 ID (임시로 하드코딩, 나중에 JWT에서 추출)
     * @param request    답변 내용이 담긴 요청 DTO
     * @return 생성된 답변을 AnswerResponse DTO로 변환한 객체
     */
    @Transactional
    public AnswerResponse createAnswer(Long questionId, Long userId, CreateAnswerRequest request) {
        // 1) 질문 존재 여부 검증 (questionId 기반)
        Question question = questionService.getQuestionById(questionId);

        // 2) 사용자 존재 여부 검증 (현재는 임시 UserRepository 사용)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 3) 이미 이 질문에 대해 해당 유저가 답변을 작성했는지 확인 (중복 방지)
        if (answerRepository.existsByQuestionIdAndUserId(questionId, userId)) {
            throw new BusinessException(ErrorCode.ALREADY_ANSWERED);
        }

        // TODO: QuestionPolicy.java의
        //       "가족 구성원 모두 답변해야 다음 질문으로 넘어갈 수 있다" 규칙은
        //       이 지점 근처에서 추가 검증 로직으로 구현할 예정입니다.

        // 4) 검증 통과 → Answer 엔티티 생성 및 저장
        Answer answer = new Answer(question, user, request.getContent());
        answer = answerRepository.save(answer);

        // 5) 저장된 엔티티를 응답 DTO로 변환해서 반환
        return AnswerResponse.of(answer);
    }

    /**
     * [역할]
     *  - 특정 질문(questionId)에 달린 모든 답변 목록을 조회합니다.
     *
     * @param questionId 답변을 조회할 질문 ID
     * @return AnswerResponse DTO 리스트
     */
    public List<AnswerResponse> getAnswersByQuestionId(Long questionId) {
        List<Answer> answers = answerRepository.findByQuestionIdOrderByCreatedAtAsc(questionId);

        return answers.stream()
                .map(AnswerResponse::of)
                .collect(Collectors.toList());
    }
}
