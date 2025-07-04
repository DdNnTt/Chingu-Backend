package com.chingubackend.service;

import com.chingubackend.dto.request.QuizCreateRequest;
import com.chingubackend.dto.response.QuizCreateResponse;
import com.chingubackend.entity.QuizSet;
import com.chingubackend.entity.QuizSetQuestion;
import com.chingubackend.entity.User;
import com.chingubackend.repository.QuestionRepository;
import com.chingubackend.repository.QuizSetQuestionRepository;
import com.chingubackend.repository.QuizSetRepository;
import com.chingubackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizSetRepository quizSetRepository;
    private final QuizSetQuestionRepository quizSetQuestionRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    @Transactional
    public QuizCreateResponse createQuiz(String creatorNickname, QuizCreateRequest request) {

        User creator = userRepository.findByNickname(creatorNickname)
                .orElseThrow(() -> new IllegalArgumentException("출제자 정보 없음"));

        QuizSet quizSet = QuizSet.builder()
                .creatorUserId(creator.getId())
                .createdAt(LocalDateTime.now())
                .build();
        quizSetRepository.save(quizSet);

        List<QuizSetQuestion> quizQuestions = request.getQuestions().stream()
                .map(q -> QuizSetQuestion.builder()
                        .quizSetId(quizSet.getId())
                        .questionId(q.getQuestionId())
                        .userSelectedAnswer(q.getSelectedAnswer())
                        .build())
                .toList();

        quizSetQuestionRepository.saveAll(quizQuestions);

        return new QuizCreateResponse(quizSet.getId(), "퀴즈 생성 완료");
    }
}