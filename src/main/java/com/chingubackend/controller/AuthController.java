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
}