package com.chingubackend.controller;

import com.chingubackend.dto.request.UserRequest;
import com.chingubackend.entity.User;
import com.chingubackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User API", description = "회원 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "사용자가 회원가입을 할 수 있습니다. 아이디, 닉네임, 이메일, 비밀번호를 입력해야 합니다.")
    public ResponseEntity<String> signup(@Valid @RequestBody UserRequest request) {
        userService.registerUser(request);
        return ResponseEntity.ok("회원가입 성공");
    }


    @GetMapping("/check-userId")
    @Operation(summary = "아이디 중복 확인", description = "아이디가 사용 가능한지 확인합니다.")
    public ResponseEntity<Boolean> checkUserId(
            @Parameter(description = "확인할 아이디", example = "user123") @RequestParam String userId) {
        boolean isAvailable = userService.isUserIdAvailable(userId);
        return ResponseEntity.ok(isAvailable);
    }

    @GetMapping("/check-nickname")
    @Operation(summary = "닉네임 중복 확인", description = "닉네임이 사용 가능한지 확인합니다.")
    public ResponseEntity<Boolean> checkNickname(
            @Parameter(description = "확인할 닉네임", example = "nickname123") @RequestParam String nickname) {
        boolean isAvailable = userService.isNicknameAvailable(nickname);
        return ResponseEntity.ok(isAvailable);
    }

    @GetMapping("/find-userId")
    @Operation(summary = "회원 아이디 찾기", description = "이름과 이메일을 입력하여 회원 아이디를 찾습니다.")
    public ResponseEntity<String> findUserId(
            @Parameter(description = "사용자 이름", example = "홍길동") @RequestParam String name,
            @Parameter(description = "사용자 이메일", example = "user@example.com") @RequestParam String email){
        String userId = userService.findUserIdByNameAndEmail(name, email);
        return ResponseEntity.ok(userId);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "유저 정보 조회", description = "유저 정보를 가져옵니다.")
    public ResponseEntity<User> getUserInfo(@Parameter(description = "유저 ID") @PathVariable String userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/delete/{userId}")
    @Operation(summary = "회원 탈퇴", description = "회원 ID를 받아 탈퇴 처리합니다.")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("회원 탈퇴 성공");
    }
}
