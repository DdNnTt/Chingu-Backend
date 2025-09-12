package com.chingubackend.service;

import com.chingubackend.dto.request.QuizCreateRequest;
import com.chingubackend.dto.request.QuizSolveRequest;
import com.chingubackend.dto.response.*;
import com.chingubackend.entity.*;
import com.chingubackend.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizSetRepository quizSetRepository;
    private final QuizSetQuestionRepository quizSetQuestionRepository;
    private final QuestionRepository questionRepository;
    private final FriendshipScoreRepository friendshipScoreRepository;
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    @Transactional
    public QuizCreateResponse createQuiz(String creatorNickname, QuizCreateRequest request) {

        User creator = userRepository.findByNickname(creatorNickname)
                .orElseThrow(() -> new IllegalArgumentException("출제자 정보가 없습니다"));

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

        return new QuizCreateResponse(quizSet.getId(), "퀴즈 생성이 완료되었습니다.");
    }

    @Transactional
    public QuizSolveResponse solveQuiz(QuizSolveRequest request) {
        User solver = userRepository.findByNickname(request.getSolverNickname())
                .orElseThrow(() -> new IllegalArgumentException("사용자 닉네임이 없습니다."));

        QuizSet quizSet = quizSetRepository.findById(request.getQuizSetId())
                .orElseThrow(() -> new IllegalArgumentException("해당 퀴즈 세트가 없습니다."));

        Long friendId = quizSet.getCreatorUserId();
        Long solverId = solver.getId();

        List<QuizSolveResponse.Result> resultList = new ArrayList<>();
        int correctCount = 0;

        for (QuizSolveRequest.Answer ans : request.getAnswers()) {
            Question question = questionRepository.findById(ans.getQuestionId())
                    .orElseThrow(() -> new IllegalArgumentException("문제 ID가 없습니다."));

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
    private void updateFriendshipScore(Long solverId, Long creatorId, int scoreToSet) {
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

        score.setScore(scoreToSet);
        score.setLastUpdated(new Timestamp(System.currentTimeMillis()));

        friendshipScoreRepository.save(score);
    }

    public QuizSetDetailResponse getQuizSetDetail(Long quizSetId) {
        QuizSet quizSet = quizSetRepository.findById(quizSetId)
                .orElseThrow(() -> new IllegalArgumentException("해당 퀴즈 세트가 없습니다."));

        User creator = userRepository.findById(quizSet.getCreatorUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        List<QuizSetQuestion> quizSetQuestions = quizSetQuestionRepository.findByQuizSetId(quizSetId);

        List<QuizSetDetailResponse.QuestionDTO> questionDTOs = quizSetQuestions.stream()
                .map(q -> {
                    Question question = questionRepository.findById(q.getQuestionId())
                            .orElseThrow(() -> new IllegalArgumentException("출제 문제가 없습니다."));
                    return QuizSetDetailResponse.QuestionDTO.builder()
                            .questionId(question.getId())
                            .content(question.getContent())
                            .option1(question.getOption1())
                            .option2(question.getOption2())
                            .option3(question.getOption3())
                            .option4(question.getOption4())
                            .build();
                })
                .toList();

        return QuizSetDetailResponse.builder()
                .quizSetId(quizSet.getId())
                .creatorNickname(creator.getNickname())
                .questions(questionDTOs)
                .build();
    }

    @Transactional
    public List<FriendScoreResponse> getMySolvedFriendScores(String myNickname) {
        User me = userRepository.findByNickname(myNickname)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        Long myId = me.getId();
        List<FriendshipScore> relatedScores = friendshipScoreRepository.findByUserIdOrFriendUserId(myId, myId);

        return relatedScores.stream()
                .map(score -> {
                    Long friendId = score.getUserId().equals(myId) ? score.getFriendUserId() : score.getUserId();

                    return userRepository.findById(friendId)
                            .map(friend -> new FriendScoreResponse(friend.getId(), friend.getNickname(), score.getScore()))
                            .orElse(null);
                })
                .filter(resp -> resp != null)
                .toList();
    }

    public List<FriendScoreResponse> getFriendshipScoresBySolver(String solverNickname) {
        User solver = userRepository.findByNickname(solverNickname)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Long myId = solver.getId();

        List<FriendshipScore> scores = friendshipScoreRepository.findByFriendUserId(myId);

        return scores.stream()
                .map(score -> {
                    User friend = userRepository.findById(score.getUserId())
                            .orElseThrow(() -> new IllegalArgumentException("친구 사용자 정보 없습니다."));

                    return new FriendScoreResponse(friend.getId(), friend.getNickname(), score.getScore());
                })
                .toList();
    }

    public List<AvailableFriendQuizResponse> getFriendsWithAvailableQuizzes(Long myUserId) {
        List<Friend> friends = friendRepository.findByUserIdOrFriendId(myUserId, myUserId);

        return friends.stream()
                .map(friend -> {
                    Long friendId = friend.getUserId().equals(myUserId)
                            ? friend.getFriendId()
                            : friend.getUserId();

                    User friendUser = userRepository.findById(friendId)
                            .orElseThrow(() -> new IllegalArgumentException("친구가 없습니다."));

                    String nickname = friendUser.getNickname();

                    Optional<QuizSet> quizSet = quizSetRepository.findFirstByCreatorUserIdOrderByCreatedAtDesc(friendId);

                    return AvailableFriendQuizResponse.builder()
                            .userId(friendId)
                            .nickname(nickname)
                            .quizSetId(quizSet.map(QuizSet::getId).orElse(null))
                            .build();
                })
                .toList();
    }

    public List<MyQuizSummaryResponse> getMyQuizzes(Long creatorUserId) {
        var quizSets = quizSetRepository.findByCreatorUserIdOrderByCreatedAtDesc(creatorUserId);

        return quizSets.stream()
                .map(qs -> MyQuizSummaryResponse.builder()
                        .quizSetId(qs.getId())
                        .createdAt(qs.getCreatedAt())
                        .questionCount(quizSetQuestionRepository.countByQuizSetId(qs.getId()))
                        .build()
                )
                .toList();
    }


}