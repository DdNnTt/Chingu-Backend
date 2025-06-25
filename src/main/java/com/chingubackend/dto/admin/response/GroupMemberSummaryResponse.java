package com.chingubackend.dto.admin.response;

import com.chingubackend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupMemberSummaryResponse {
    private Long userId;
    private String name;
    private String nickname;
    private String email;

    public static GroupMemberSummaryResponse from(User user) {
        return new GroupMemberSummaryResponse(
                user.getId(),
                user.getName(),
                user.getNickname(),
                user.getEmail()
        );
    }
}