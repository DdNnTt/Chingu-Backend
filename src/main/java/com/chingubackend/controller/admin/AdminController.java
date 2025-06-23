package com.chingubackend.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/admin")
public class AdminController {

    @Operation(summary = "관리자 권한 확인", description = "로그인한 사용자가 ROLE_ADMIN 권한을 가졌는지 확인")
    @GetMapping("/check")
    public ResponseEntity<?> checkAdmin(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("관리자 권한이 필요합니다.");
        }
        return ResponseEntity.ok("접근 가능");
    }
}