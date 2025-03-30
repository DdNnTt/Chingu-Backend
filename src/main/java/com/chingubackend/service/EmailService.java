package com.chingubackend.service;

import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final StringRedisTemplate redisTemplate;

    // 인증 코드 생성
    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6자리 숫자 생성
        return String.valueOf(code);
    }

    // Redis에 인증 코드 저장 (유효기간 5분)
    public void saveVerificationCode(String email, String code) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set("email:" + email, code, 5, TimeUnit.MINUTES);
    }

    // 인증 코드 검증
    public boolean verifyCode(String email, String code) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String storedCode = ops.get("email:" + email);
        return storedCode != null && storedCode.equals(code);
    }
}