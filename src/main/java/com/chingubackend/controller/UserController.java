package com.chingubackend.controller;

import com.chingubackend.dto.request.SignupRequest;
import com.chingubackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "사용자가 회원가입을 할 수 있습니다. 아이디, 닉네임, 이메일, 비밀번호를 입력해야 합니다.")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest request) {
        userService.registerUser(request);
        return ResponseEntity.ok("회원가입 성공");
    }

    @GetMapping("/check-userId")
    public ResponseEntity<Boolean> checkUserId(@RequestParam String userId) {
        boolean isAvailable = userService.isUserIdAvailable(userId);
        return ResponseEntity.ok(isAvailable);
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
        boolean isAvailable = userService.isNicknameAvailable(nickname);
        return ResponseEntity.ok(isAvailable);
    }
}
