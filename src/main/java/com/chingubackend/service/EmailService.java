package com.chingubackend.service;

import com.chingubackend.entity.User;
import com.chingubackend.repository.UserRepository;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    public boolean changePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        String encodedPassword = passwordEncoder.encode(newPassword);

        user.setPassword(encodedPassword);
        userRepository.save(user);

        return true;
    }
}