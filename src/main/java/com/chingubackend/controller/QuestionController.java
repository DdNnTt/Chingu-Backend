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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "QUIZ API", description = "나를 맞춰봐 관련 API")
public class QuestionController {

    private final QuestionService questionService;
    private final QuizService quizService;
    private final UserRepository userRepository;
    private final FriendshipScoreRepository friendshipScoreRepository;

    @Operation(summary = "전체 문제 조회", description = "등록된 모든 퀴즈 문제를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/question-all")
    public ResponseEntity<List<QuestionResponse>> getAllQuestions() {
        List<QuestionResponse> questions = questionService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }

    @Operation(summary = "랜덤 문제 10개 조회", description = "등록된 퀴즈 문제 중 랜덤으로 10개를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/question-random")
    public ResponseEntity<List<QuestionResponse>> getRandomTenQuestions() {
        List<QuestionResponse> questions = questionService.getRandomTenQuestions();
        return ResponseEntity.ok(questions);
    }

    @Operation(summary = "퀴즈 세트 상세 조회", description = "퀴즈 세트 ID로 세트 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{quizSetId}")
    public ResponseEntity<QuizSetDetailResponse> getQuizSetDetail(@PathVariable Long quizSetId) {
        QuizSetDetailResponse response = quizService.getQuizSetDetail(quizSetId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "내가 만든 퀴즈 목록 조회",
            description = "로그인한 사용자가 생성한 퀴즈 세트를 최신순으로 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/my-quizzes")
    public ResponseEntity<List<MyQuizSummaryResponse>> getMyQuizzes(
            @AuthenticationPrincipal UserDetails userDetails) {

        String userId = userDetails.getUsername();
        User me = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        var responses = quizService.getMyQuizzes(me.getId());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "퀴즈 생성", description = "친구가 맞춰볼 수 있도록 퀴즈를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "퀴즈 생성 성공")
    @PostMapping("/create")
    public ResponseEntity<QuizCreateResponse> createQuiz(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid QuizCreateRequest request) {

        String creatorNickname = userDetails.getUsername();
        QuizCreateResponse response = quizService.createQuiz(creatorNickname, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "퀴즈 풀이", description = "친구가 만든 퀴즈를 풉니다.")
    @ApiResponse(responseCode = "201", description = "퀴즈 풀이 완료 및 점수 계산")
    @PostMapping("/solve")
    public ResponseEntity<QuizSolveResponse> solveQuiz(@RequestBody QuizSolveRequest request) {
        QuizSolveResponse response = quizService.solveQuiz(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "우정 점수 조회", description = "나와 친구 간의 우정 점수를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/scores")
    public ResponseEntity<List<FriendScoreResponse>> getFriendshipScores(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        Long myId = user.getId();

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

    @Operation(
            summary = "문제 만든 친구 목록 조회",
            description = "친구 목록 중 퀴즈를 만든 사용자의 목록을 반환합니다. 퀴즈가 없는 경우 quizSetId는 null입니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
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