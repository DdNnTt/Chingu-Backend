package com.chingubackend.dto.admin.response;


import com.chingubackend.entity.User;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class AdminUserResponse {
    private Long userId;
    private String name;
    private String nickname;
    private String email;
    private LocalDateTime joinDate;
    private LocalDateTime lastLoginDate;

    public AdminUserResponse(User user) {
        this.userId = user.getId();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.joinDate = user.getJoinDate();
        this.lastLoginDate = user.getLastLoginDate();
    }
}
