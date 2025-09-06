package com.chingubackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class MyQuizSummaryResponse {
    private Long quizSetId;
    private LocalDateTime createdAt;
    private int questionCount;
}