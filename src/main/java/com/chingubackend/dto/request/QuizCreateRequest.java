package com.chingubackend.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class QuizCreateRequest {

    @NotNull
    private List<QuizQuestionRequest> questions;

    @Getter
    @NoArgsConstructor
    public static class QuizQuestionRequest {
        @NotNull
        private Long questionId;

        @NotNull
        @Min(1)
        @Max(4)
        private Integer selectedAnswer;
    }
}
