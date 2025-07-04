package com.chingubackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuizCreateResponse {
    private Long quizSetId;
    private String message;
}
