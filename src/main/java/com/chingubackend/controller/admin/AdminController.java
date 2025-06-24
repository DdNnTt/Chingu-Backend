package com.chingubackend.controller.admin;

import com.chingubackend.dto.admin.response.AdminUserDeleteResponse;
import com.chingubackend.dto.admin.response.AdminUserResponse;
import com.chingubackend.entity.User;
import com.chingubackend.exception.UserNotFoundException;
import com.chingubackend.repository.UserRepository;
import com.chingubackend.service.admin.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chingubackend.security.CustomUserDetails;

@Tag(name = "Admin", description = "관리자 전용 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final AdminUserService adminUserService;

    @Operation(summary = "관리자 권한 확인", description = "로그인한 사용자가 ROLE_ADMIN 권한을 가졌는지 확인")
    @GetMapping("/check")
    public ResponseEntity<?> checkAdmin(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("관리자 권한이 필요합니다.");
        }
        return ResponseEntity.ok("접근 가능");
    }

    @Operation(summary = "전체 회원 목록 조회", description = "관리자 권한을 가진 사용자만 전체 회원 정보를 조회할 수 있습니다.")
    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponse>> getAllUsers(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("관리자 권한이 필요합니다.");
        }

        List<AdminUserResponse> users = userRepository.findAll().stream()
                .map(AdminUserResponse::new)
                .toList();

        return ResponseEntity.ok(users);
    }

    @Operation(summary = "회원 검색", description = "이름, 닉네임, 사용자 ID를 기준으로 회원을 검색합니다. 관리자 권한 필요.")
    @GetMapping("/users/search")
    public ResponseEntity<List<AdminUserResponse>> searchUsersByAdmin(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "검색 키워드 (이름/닉네임/ID)")
            @RequestParam String keyword) {

        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("관리자 권한이 필요합니다.");
        }

        List<User> users = userRepository.searchByKeyword(keyword);

        if (users.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        List<AdminUserResponse> response = users.stream()
                .map(AdminUserResponse::new)
                .toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/{userId}")
    @Operation(summary = "회원 삭제", description = "관리자가 특정 사용자를 삭제합니다.")
    public ResponseEntity<AdminUserDeleteResponse> deleteUserByAdmin(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long userId) {

        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("관리자 권한이 필요합니다.");
        }

        adminUserService.deleteUserByAdmin(userId);

        return ResponseEntity.ok(new AdminUserDeleteResponse(userId, "회원 삭제 성공"));
    }


}