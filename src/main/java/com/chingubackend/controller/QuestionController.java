package com.chingubackend.controller;


import com.chingubackend.dto.request.QuizCreateRequest;
import com.chingubackend.dto.request.QuizSolveRequest;
import com.chingubackend.dto.response.*;
import com.chingubackend.entity.FriendshipScore;
import com.chingubackend.entity.User;
import com.chingubackend.repository.FriendshipScoreRepository;
import com.chingubackend.repository.UserRepository;
import com.chingubackend.service.QuestionService;
import com.chingubackend.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final QuizService quizService;
    private final UserRepository userRepository;
    private final FriendshipScoreRepository friendshipScoreRepository;

    @GetMapping("/question-all")
    public ResponseEntity<List<QuestionResponse>> getAllQuestions() {
        List<QuestionResponse> questions = questionService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/question-random")
    public ResponseEntity<List<QuestionResponse>> getRandomTenQuestions() {
        List<QuestionResponse> questions = questionService.getRandomTenQuestions();
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/{quizSetId}")
    public ResponseEntity<QuizSetDetailResponse> getQuizSetDetail(@PathVariable Long quizSetId) {
        QuizSetDetailResponse response = quizService.getQuizSetDetail(quizSetId);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/create")
    public ResponseEntity<QuizCreateResponse> createQuiz(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid QuizCreateRequest request) {

        String creatorNickname = userDetails.getUsername();
        QuizCreateResponse response = quizService.createQuiz(creatorNickname, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/solve")
    public ResponseEntity<QuizSolveResponse> solveQuiz(@RequestBody QuizSolveRequest request) {
        QuizSolveResponse response = quizService.solveQuiz(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/scores")
    public ResponseEntity<List<FriendScoreResponse>> getFriendshipScores(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        log.info("[Nickname Check] userId from token: {}", userId);

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        Long myId = user.getId();
        log.info("[ID Check] myId: {}", myId);

        List<FriendshipScore> scores = friendshipScoreRepository.findByUserIdOrFriendUserId(myId, myId);

        List<FriendScoreResponse> response = scores.stream()
                .map(score -> {
                    Long friendId = score.getUserId().equals(myId) ? score.getFriendUserId() : score.getUserId();
                    User friend = userRepository.findById(friendId)
                            .orElseThrow(() -> new IllegalArgumentException("상대 사용자 없음"));

                    return new FriendScoreResponse(friend.getId(), friend.getNickname(), score.getScore());
                })
                .toList();

        return ResponseEntity.ok(response);
    }


    @GetMapping("/friends-available")
    public ResponseEntity<List<AvailableFriendQuizResponse>> getFriendsWithAvailableQuizzes(
            @AuthenticationPrincipal UserDetails userDetails) {

        String userId = userDetails.getUsername();
        User me = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        List<AvailableFriendQuizResponse> responses = quizService.getFriendsWithAvailableQuizzes(me.getId());
        return ResponseEntity.ok(responses);
    }
}
