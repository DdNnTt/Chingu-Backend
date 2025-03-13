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

    @Transactional
    public boolean isUserIdAvailable(String userId) {
        return userRepository.findByUserId(userId).isEmpty();
    }

    @Transactional
    public boolean isNicknameAvailable(String nickname) {
        return userRepository.findByNickname(nickname).isEmpty();
    }

}
