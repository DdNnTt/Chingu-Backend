package com.chingubackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequest {
    private Long friendId;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PendingRequest {
        private Long fromUserId;
        private String nickname;
        private java.sql.Timestamp requestedAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseRequest {
        private Long friendId;
        private String status;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FriendList {
        private Long friendUserId;
        private String nickname;
        private String name;
        private int score;
        private Timestamp friendSince;
    }

}
