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
}