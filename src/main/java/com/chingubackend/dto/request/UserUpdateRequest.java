package com.chingubackend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
    private String nickname;
    private String profilePictureUrl;
    private String bio;
    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;
}
