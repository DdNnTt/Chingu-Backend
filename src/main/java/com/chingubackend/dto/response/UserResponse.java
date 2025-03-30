package com.chingubackend.dto.response;

import com.chingubackend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String nickname;
    private String profilePictureUrl;

    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getNickname(),
                user.getProfilePictureUrl()
        );
    }
}
