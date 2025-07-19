package com.chingubackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendScoreResponse {
    private Long friendId;
    private String friendNickname;
    private int score;
}
