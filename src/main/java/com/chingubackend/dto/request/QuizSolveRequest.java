package com.chingubackend.dto.request;

import lombok.Getter;
import java.util.List;

@Getter
public class QuizSolveRequest {

    private String solverNickname;
    private Long quizSetId;
    private List<Answer> answers;

    @Getter
    public static class Answer {
        private Long questionId;
        private Integer selectedOption;
    }
}