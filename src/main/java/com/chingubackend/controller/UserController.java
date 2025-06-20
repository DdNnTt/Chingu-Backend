package com.chingubackend.controller;

import com.chingubackend.dto.request.DeleteUserRequest;
import com.chingubackend.dto.request.UserRequest;
import com.chingubackend.dto.request.UserUpdateRequest;
import com.chingubackend.dto.response.UserResponse;
import com.chingubackend.entity.User;
import com.chingubackend.service.S3Service;
import com.chingubackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
    private final S3Service s3Service;

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

    @DeleteMapping("/delete")
    @Operation(summary = "회원 탈퇴", description = "비밀번호 확인 후 회원 탈퇴를 진행합니다.")
    public ResponseEntity<String> deleteUser(@Valid @RequestBody DeleteUserRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        userService.deleteUser(userId, request);
        return ResponseEntity.ok("회원 탈퇴 성공");
    }

    @GetMapping("/mypage")
    @Operation(summary = "마이페이지 조회", description = "로그인한 사용자의 마이페이지 정보를 조회합니다.")
    public ResponseEntity<User> getMyPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.getUserById(username);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "회원 검색", description = "이름, 닉네임, 회원 아이디로 회원을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchUsers(
            @Parameter(description = "검색어 (이름, 닉네임, 회원 아이디 포함)")
            @RequestParam String keyword) {
        List<UserResponse> users = userService.searchUsers(keyword);

        Map<String, Object> response = new HashMap<>();
        response.put("users", users);

        if (users.isEmpty()) {
            response.put("message", "일치하는 회원이 없습니다.");
        }

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/mypage/edit")
    @Operation(summary = "마이페이지 수정", description = "로그인한 사용자가 닉네임, 프로필 사진, 자기소개, 비밀번호를 수정합니다.")
    public ResponseEntity<String> updateMyPage(@RequestBody UserUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        userService.updateMyPage(userId, request);
        return ResponseEntity.ok("마이페이지가 성공적으로 수정되었습니다.");
    }

    @PostMapping("/upload-url/profile")
    @Operation(summary = "프로필 사진 업로드용 S3 URL 발급", description = "JPG, PNG 확장자를 지원하며 기본값은 JPG입니다.")
    public ResponseEntity<Map<String, String>> getProfileUploadUrl(
            @RequestParam(defaultValue = "jpg") String extension
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        String ext = extension.startsWith(".") ? extension : "." + extension; // ".png" 형식 보장
        String key = "profile/" + userId + "-" + UUID.randomUUID() + ext;

        URL uploadUrl = s3Service.generatePreSignedUrl(key);
        String fileUrl = s3Service.getFileUrl(key);

        return ResponseEntity.ok(Map.of(
                "uploadUrl", uploadUrl.toString(),
                "fileUrl", fileUrl
        ));
    }
}
