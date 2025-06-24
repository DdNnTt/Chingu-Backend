package com.chingubackend.dto.admin.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminUserDeleteResponse {
    private Long userId;
    private String message;
}
