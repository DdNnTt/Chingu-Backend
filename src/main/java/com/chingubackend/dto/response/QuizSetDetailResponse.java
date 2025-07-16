package com.chingubackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class QuizSetDetailResponse {
    private Long quizSetId;
    private String creatorNickname;
    private List<QuestionDTO> questions;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class QuestionDTO {
        private Long questionId;
        private String content;
        private String option1;
        private String option2;
        private String option3;
        private String option4;
    }
}
