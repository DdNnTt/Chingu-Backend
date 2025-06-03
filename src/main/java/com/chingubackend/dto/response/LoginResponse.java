package com.chingubackend.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String accessToken;
    private String tokenType;
    private String nickname;
    private String email;

    public LoginResponse(String accessToken, String tokenType, String nickname, String email) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.nickname = nickname;
        this.email = email;
    }
}