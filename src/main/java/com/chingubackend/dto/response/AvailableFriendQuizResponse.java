package com.chingubackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AvailableFriendQuizResponse {
    private Long userId;
    private String nickname;
    private Long quizSetId;
}
