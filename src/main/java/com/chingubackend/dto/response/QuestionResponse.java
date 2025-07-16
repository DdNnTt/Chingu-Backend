package com.chingubackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuestionResponse {
    private Long id;
    private String content;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
}
