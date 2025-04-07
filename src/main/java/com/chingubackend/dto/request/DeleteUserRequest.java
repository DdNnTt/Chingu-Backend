package com.chingubackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteUserRequest {

    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    private String password;
}
