package com.chingubackend.service;

import com.chingubackend.dto.request.SignupRequest;
import com.chingubackend.entity.User;
import com.chingubackend.model.SocialType;
import com.chingubackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(SignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }

        if (userRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new IllegalStateException("이미 사용 중인 아이디입니다.");
        }

        if (userRepository.findByNickname(request.getNickname()).isPresent()) {
            throw new IllegalStateException("이미 사용 중인 닉네임입니다.");
        }

        User user = User.builder()
                .userId(request.getUserId())
                .name(request.getName())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .profilePictureUrl(request.getProfilePictureUrl())
                .bio(request.getBio())
                .socialType(SocialType.NONE)
                .build();

        userRepository.save(user);
    }

}
