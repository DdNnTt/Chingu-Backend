package com.chingubackend.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GroupInviteResponse {
    private Long requestId;
    private Long friendUserId;
    private String nickname;
    private String name;
    private String requestStatus;
    private LocalDateTime createdAt;
    private Long groupId;

    @Getter
    @Builder
    public static class GroupInviteResponseWithoutFriend {
        private Long requestId;
        private String nickname;
        private String name;
        private String requestStatus;
        private LocalDateTime createdAt;
        private Long groupId;
    }

    public GroupInviteResponseWithoutFriend toResponseWithoutFriend() {
        return GroupInviteResponseWithoutFriend.builder()
                .requestId(this.requestId)
                .nickname(this.nickname)
                .name(this.name)
                .requestStatus(this.requestStatus)
                .createdAt(this.createdAt)
                .groupId(this.groupId)
                .build();
    }
}