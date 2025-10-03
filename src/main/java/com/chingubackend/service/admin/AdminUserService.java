package com.chingubackend.service.admin;

import com.chingubackend.entity.User;
import com.chingubackend.exception.UserNotFoundException;
import com.chingubackend.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final MessageRepository messageRepository;
    private final GroupInviteRepository groupInviteRepository;
    private final ScheduleRepository scheduleRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupScheduleRepository groupScheduleRepository;

    @Transactional
    public void deleteUserByAdmin(Long targetUserId) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(UserNotFoundException::new);

        User deletedUser = userRepository.findByUserId("deleted-user")
                .orElseThrow(() -> new IllegalStateException("시스템 사용자(deleted-user)가 존재하지 않습니다."));


        groupInviteRepository.deleteBySenderId(user.getId());
        groupInviteRepository.deleteByReceiverId(user.getId());

        groupRepository.updateCreatorId(user.getId(), deletedUser.getId());
        messageRepository.deleteBySenderIdOrReceiverId(user.getId(), user.getId());

        scheduleRepository.deleteByUser(user);
        groupMemberRepository.deleteByUserId(user.getId());
        groupScheduleRepository.deleteByUser(user);

        userRepository.delete(user);
    }
}