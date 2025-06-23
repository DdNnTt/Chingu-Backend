package com.chingubackend.service;

import com.chingubackend.dto.request.DeleteUserRequest;
import com.chingubackend.dto.request.UserRequest;
import com.chingubackend.dto.request.UserUpdateRequest;
import com.chingubackend.dto.response.UserResponse;
import com.chingubackend.entity.User;
import com.chingubackend.exception.DuplicateNicknameException;
import com.chingubackend.exception.EmailNotVerifiedException;
import com.chingubackend.exception.PasswordMismatchException;
import com.chingubackend.exception.UserNotFoundException;
import com.chingubackend.model.Role;
import com.chingubackend.model.SocialType;
import com.chingubackend.repository.GroupInviteRepository;
import com.chingubackend.repository.GroupMemberRepository;
import com.chingubackend.repository.GroupRepository;
import com.chingubackend.repository.GroupScheduleRepository;
import com.chingubackend.repository.MessageRepository;
import com.chingubackend.repository.ScheduleRepository;
import com.chingubackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
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
    private final GroupMemberRepository groupMemberRepository;
    private final GroupInviteRepository groupInviteRepository;
    private final GroupScheduleRepository groupScheduleRepository;
    private final GroupRepository groupRepository;
    private final MessageRepository messageRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public void registerUser(UserRequest request) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String verified = ops.get("email:verified:" + request.getEmail());

        if (verified == null || !verified.equals("true")) {
            throw new EmailNotVerifiedException();
        }

        String profilePictureUrl = request.getProfilePictureUrl() != null
                ? request.getProfilePictureUrl()
                : "https://chingu-album.s3.ap-northeast-2.amazonaws.com/album/default-profile.png";

        User user = User.builder()
                .userId(request.getUserId())
                .name(request.getName())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .profilePictureUrl(profilePictureUrl)
                .bio(request.getBio())
                .socialType(SocialType.NONE)
                .role(Role.ROLE_USER)
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
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public User getUserById(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public void deleteUser(String userId, DeleteUserRequest request) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }

        User deletedUser = userRepository.findByUserId("deleted-user")
                .orElseThrow(() -> new IllegalStateException("시스템 사용자(deleted-user)가 존재하지 않습니다."));

        // 탈퇴 사용자가 생성한 그룹의 소유자를 시스템 사용자로 이전
        groupRepository.updateCreatorId(user.getId(), deletedUser.getId());

        messageRepository.deleteBySenderIdOrReceiverId(user.getId(), user.getId());
        groupInviteRepository.deleteBySenderIdOrReceiverId(user.getId(), user.getId());
        scheduleRepository.deleteByUser(user);
        groupMemberRepository.deleteByUserId(user.getId());
        groupScheduleRepository.deleteByUser(user);

        userRepository.delete(user);
    }

    public List<UserResponse> searchUsers(String keyword) {
        List<User> users = userRepository.findByNameOrNicknameOrUserId(keyword, keyword, keyword);
        return users.stream().map(UserResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public void updateMyPage(String userId, UserUpdateRequest request) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(UserNotFoundException::new);

        // 닉네임 수정 (중복 확인)
        if (request.getNickname() != null && !request.getNickname().equals(user.getNickname())) {
            if (userRepository.findByNickname(request.getNickname()).isPresent()) {
                throw new DuplicateNicknameException();
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
        if (isPasswordChangeRequested(request)) {
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new PasswordMismatchException("현재 비밀번호가 일치하지 않습니다.");
            }

            if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
                throw new PasswordMismatchException("새 비밀번호가 일치하지 않습니다.");
            }

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        userRepository.save(user);
    }

    @Transactional
    public void updateLastLoginDate(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(UserNotFoundException::new);
        user.setLastLoginDate(LocalDateTime.now());
        userRepository.save(user);
    }

    private boolean isPasswordChangeRequested(UserUpdateRequest request) {
        return request.getCurrentPassword() != null &&
                request.getNewPassword() != null &&
                request.getConfirmNewPassword() != null;
    }
}