package com.chingubackend.controller.admin;

import com.chingubackend.dto.admin.response.AdminUserResponse;
import com.chingubackend.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chingubackend.security.CustomUserDetails;

@Tag(name = "Admin", description = "관리자 전용 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;

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
}