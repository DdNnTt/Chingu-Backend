package com.chingubackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequest {
    private Long userId;
    private Long friendId;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PendingRequestDto {
        private Long fromUserId;
        private String nickname;
        private java.sql.Timestamp requestedAt;
    }
}
