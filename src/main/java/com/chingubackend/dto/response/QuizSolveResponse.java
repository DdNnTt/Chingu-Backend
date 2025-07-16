package com.chingubackend.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class QuizSolveResponse {
    private Long quizSetId;
    private String solverNickname;
    private int totalCorrect;
    private int totalQuestions;
    private int totalScore;
    private List<Result> results;

    @Getter
    @AllArgsConstructor
    public static class Result {
        private Long questionId;
        private int selectedOption;
        private boolean isCorrect;
    }
}