package com.chingubackend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GroupMemberResponse {
    private Long userId;
    private String name;
    private String nickname;
    private String email;
}
