package com.chingubackend.service;

import com.chingubackend.dto.request.QuizCreateRequest;
import com.chingubackend.dto.request.QuizSolveRequest;
import com.chingubackend.dto.response.QuizCreateResponse;
import com.chingubackend.dto.response.QuizSolveResponse;
import com.chingubackend.entity.*;
import com.chingubackend.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizSetRepository quizSetRepository;
    private final QuizSetQuestionRepository quizSetQuestionRepository;
    private final QuestionRepository questionRepository;
    private final FriendshipScoreRepository friendshipScoreRepository;
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

    @Transactional
    public QuizSolveResponse solveQuiz(QuizSolveRequest request) {
        User solver = userRepository.findByNickname(request.getSolverNickname())
                .orElseThrow(() -> new IllegalArgumentException("사용자 닉네임 없음"));

        QuizSet quizSet = quizSetRepository.findById(request.getQuizSetId())
                .orElseThrow(() -> new IllegalArgumentException("해당 퀴즈 세트 없음"));

        Long friendId = quizSet.getCreatorUserId();
        Long solverId = solver.getId();

        List<QuizSolveResponse.Result> resultList = new ArrayList<>();
        int correctCount = 0;

        for (QuizSolveRequest.Answer ans : request.getAnswers()) {
            Question question = questionRepository.findById(ans.getQuestionId())
                    .orElseThrow(() -> new IllegalArgumentException("문제 ID 없음"));

            boolean isCorrect = question.getCorrectAnswer().equals(ans.getSelectedOption());
            if (isCorrect) correctCount++;

            resultList.add(new QuizSolveResponse.Result(
                    question.getId(),
                    ans.getSelectedOption(),
                    isCorrect
            ));
        }

        int score = correctCount * 10;
        updateFriendshipScore(solverId, friendId, score);

        return new QuizSolveResponse(
                quizSet.getId(),
                solver.getNickname(),
                correctCount,
                request.getAnswers().size(),
                score,
                resultList
        );
    }
    private void updateFriendshipScore(Long solverId, Long creatorId, int scoreToAdd) {
        Long userA = Math.min(solverId, creatorId);
        Long userB = Math.max(solverId, creatorId);

        FriendshipScore score = friendshipScoreRepository
                .findByUserIdAndFriendUserId(userA, userB)
                .orElse(FriendshipScore.builder()
                        .userId(userA)
                        .friendUserId(userB)
                        .score(0)
                        .lastUpdated(new Timestamp(System.currentTimeMillis()))
                        .build()
                );

        score.setScore(score.getScore() + scoreToAdd);
        score.setLastUpdated(new Timestamp(System.currentTimeMillis()));

        friendshipScoreRepository.save(score);
    }
}