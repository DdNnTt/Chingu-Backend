package com.chingubackend.controller;

import com.chingubackend.service.EmailService;
import com.chingubackend.service.MailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/email")
@RequiredArgsConstructor
public class AuthController {
    private final EmailService emailService;
    private final MailService mailService;
    private final RedisTemplate<String, String> redisTemplate;

    @Operation(summary = "이메일 인증 코드 전송", description = "사용자가 입력한 이메일로 인증 코드를 전송합니다.")
    @PostMapping("/verify")
    public ResponseEntity<String> sendVerificationCode(
            @Parameter(description = "인증을 받을 이메일") @RequestParam String email) {
        String code = emailService.generateVerificationCode();
        emailService.saveVerificationCode(email, code);
        mailService.sendEmail(email, "이메일 인증 코드", "인증 코드: " + code);

        return ResponseEntity.ok("인증 코드가 이메일로 전송되었습니다.");
    }

    @Operation(summary = "이메일 인증 코드 확인", description = "사용자가 입력한 인증 코드를 확인하고, 이메일 인증을 완료합니다.")
    @PostMapping("/confirm")
    public ResponseEntity<String> confirmVerificationCode(
            @Parameter(description = "인증을 받을 이메일") @RequestParam String email,
            @Parameter(description = "사용자가 입력한 인증 코드") @RequestParam String code) {
        boolean isValid = emailService.verifyCode(email, code);
        if (!isValid) {
            return ResponseEntity.badRequest().body("인증 코드가 일치하지 않습니다.");
        }

        // 이메일 인증이 완료되었으면 Redis에 저장
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set("email:verified:" + email, "true", 10, TimeUnit.MINUTES); // 인증 완료 시간 설정

        return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
    }

    @Operation(summary = "비밀번호 찾기 인증 코드 전송", description = "비밀번호 찾기를 위해 이메일로 인증 코드를 전송합니다.")
    @PostMapping("/password/verify")
    public ResponseEntity<String> sendPasswordResetVerificationCode(
            @Parameter(description = "비밀번호를 찾을 이메일") @RequestParam String email) {
        String code = emailService.generateVerificationCode();
        emailService.saveVerificationCode(email, code);
        mailService.sendEmail(email, "비밀번호 찾기 인증 코드", "인증 코드: " + code);

        return ResponseEntity.ok("인증 코드가 이메일로 전송되었습니다.");
    }

    @Operation(summary = "비밀번호 변경", description = "인증 코드 확인 후 새로운 비밀번호를 설정합니다.")
    @PostMapping("/password/reset")
    public ResponseEntity<String> resetPassword(
            @Parameter(description = "비밀번호를 찾을 이메일") @RequestParam String email,
            @Parameter(description = "새 비밀번호") @RequestParam String newPassword,
            @Parameter(description = "입력한 인증 코드") @RequestParam String code) {

        // 인증 코드 검증
        boolean isValid = emailService.verifyCode(email, code);
        if (!isValid) {
            return ResponseEntity.badRequest().body("인증 코드가 일치하지 않습니다.");
        }

        // 이메일 인증 상태가 완료되었는지 확인
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String verificationStatus = ops.get("email:verified:" + email);
        if (verificationStatus == null || !verificationStatus.equals("true")) {
            return ResponseEntity.badRequest().body("이메일 인증이 완료되지 않았습니다.");
        }

        // 비밀번호 변경 처리 (이메일을 통해 비밀번호를 찾은 후 새로운 비밀번호로 변경)
        boolean isPasswordChanged = emailService.changePassword(email, newPassword);
        if (!isPasswordChanged) {
            return ResponseEntity.status(500).body("비밀번호 변경에 실패했습니다.");
        }

        // 인증 완료 상태 삭제 (새 비밀번호 변경 후)
        redisTemplate.delete("email:verified:" + email);

        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }
}