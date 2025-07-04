package com.chingubackend.controller;


import com.chingubackend.dto.request.QuizCreateRequest;
import com.chingubackend.dto.request.QuizSolveRequest;
import com.chingubackend.dto.response.QuestionResponse;
import com.chingubackend.dto.response.QuizCreateResponse;
import com.chingubackend.dto.response.QuizSolveResponse;
import com.chingubackend.service.QuestionService;
import com.chingubackend.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final QuizService quizService;

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

}
