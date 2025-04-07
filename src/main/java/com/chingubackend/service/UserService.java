package com.chingubackend.service;

import com.chingubackend.dto.request.UserRequest;
import com.chingubackend.dto.request.UserUpdateRequest;
import com.chingubackend.dto.response.UserResponse;
import com.chingubackend.entity.User;
import com.chingubackend.model.SocialType;
import com.chingubackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void registerUser(UserRequest request) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String verified = ops.get("email:verified:" + request.getEmail());

        if (verified == null || !verified.equals("true")) {
            throw new IllegalStateException("이메일 인증이 필요합니다.");
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

    @Transactional
    public void deleteUser(String userId) {
        if (!userRepository.findByUserId(userId).isPresent()) {
            throw new IllegalStateException("존재하지 않는 사용자입니다.");
        }
        userRepository.deleteByUserId(userId);
    }

    public List<UserResponse> searchUsers(String keyword) {
        List<User> users = userRepository.findByNameOrNicknameOrUserId(keyword, keyword, keyword);
        return users.stream().map(UserResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public void updateMyPage(String userId, UserUpdateRequest request) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 닉네임 수정 (중복 확인)
        if (request.getNickname() != null && !request.getNickname().equals(user.getNickname())) {
            if (userRepository.findByNickname(request.getNickname()).isPresent()) {
                throw new IllegalStateException("이미 사용 중인 닉네임입니다.");
            }
            user.setNickname(request.getNickname());
        }

        // 프로필 사진 수정
        if (request.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(request.getProfilePictureUrl());
        }

        // 자기소개 수정
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        // 비밀번호 수정
        if (request.getCurrentPassword() != null &&
                request.getNewPassword() != null &&
                request.getConfirmNewPassword() != null) {

            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new IllegalStateException("현재 비밀번호가 일치하지 않습니다.");
            }

            if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
                throw new IllegalStateException("새 비밀번호가 일치하지 않습니다.");
            }

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        userRepository.save(user);
    }
}
