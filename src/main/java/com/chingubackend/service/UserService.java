package com.chingubackend.service;

import com.chingubackend.dto.request.UserRequest;
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
    public void registerUser(UserRequest request) {
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

    @Transactional
    public String findUserIdByNameAndEmail(String name, String email) {
        return userRepository.findByNameAndEmail(name, email)
                .map(User::getUserId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public User getUserById(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
    }

    public void deleteUser(String userId) {
        if (!userRepository.findByUserId(userId).isPresent()) {
            throw new IllegalStateException("존재하지 않는 사용자입니다.");
        }
        userRepository.deleteByUserId(userId);
    }

}
