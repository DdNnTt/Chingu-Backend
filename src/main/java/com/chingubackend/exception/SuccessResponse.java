package com.chingubackend.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SuccessResponse {
    private final String message;
    private final LocalDateTime timestamp;

    public static SuccessResponse of(String message) {
        return SuccessResponse.builder()
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
