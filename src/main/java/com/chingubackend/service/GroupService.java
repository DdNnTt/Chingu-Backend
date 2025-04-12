package com.chingubackend.service;

import com.chingubackend.dto.request.GroupRequest;
import com.chingubackend.dto.response.GroupDeleteResponse;
import com.chingubackend.dto.response.GroupInviteResponse;
import com.chingubackend.dto.response.GroupResponse;
import com.chingubackend.entity.Friend;
import com.chingubackend.entity.Group;
import com.chingubackend.entity.GroupInvite;
import com.chingubackend.entity.User;
import com.chingubackend.model.RequestStatus;
import com.chingubackend.repository.FriendRepository;
import com.chingubackend.repository.GroupInviteRepository;
import com.chingubackend.repository.GroupRepository;
import com.chingubackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final GroupInviteRepository groupInviteRepository;

    public GroupService(GroupRepository groupRepository,
                        UserRepository userRepository,
                        FriendRepository friendRepository,
                        GroupInviteRepository groupInviteRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
        this.groupInviteRepository = groupInviteRepository;
    }

    public GroupResponse createGroup(GroupRequest request, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Group group = Group.builder()
                .groupName(request.getGroupName())
                .description(request.getDescription())
                .creator(user)
                .build();

        Group saved = groupRepository.save(group);

        return GroupResponse.builder()
                .groupId(saved.getId())
                .groupName(saved.getGroupName())
                .description(saved.getDescription())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Transactional
    public GroupDeleteResponse deleteGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("해당 그룹을 찾을 수 없습니다."));

        if (!group.getCreator().getId().equals(userId)) {
            throw new AccessDeniedException("그룹 생성자만 삭제할 수 있습니다.");
        }

        groupRepository.delete(group);

        return GroupDeleteResponse.builder()
                .groupId(groupId)
                .deleted(true)
                .build();
    }

    public List<GroupInviteResponse> inviteFriendsToGroup(Long groupId, Long userId, List<Long> friendUserIds) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("그룹을 찾을 수 없습니다."));

        User inviter = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        return friendUserIds.stream()
                .map(friendUserId -> {
                    Optional<Friend> acceptedFriend = friendRepository.findAcceptedFriend(userId, friendUserId);
                    if (acceptedFriend.isPresent()) {
                        Optional<GroupInvite> existingInvite =
                                groupInviteRepository.findByGroupIdAndSenderIdAndReceiverId(groupId, userId, friendUserId);

                        if (existingInvite.isPresent()) {
                            return GroupInviteResponse.builder()
                                    .requestId(existingInvite.get().getId())
                                    .friendUserId(friendUserId)
                                    .nickname(existingInvite.get().getReceiver().getNickname())
                                    .name(existingInvite.get().getReceiver().getName())
                                    .requestStatus("이미 초대한 친구입니다.")
                                    .createdAt(existingInvite.get().getCreatedAt())
                                    .build();
                        } else {
                            User receiver = userRepository.findById(friendUserId)
                                    .orElseThrow(() -> new EntityNotFoundException("초대 대상 사용자를 찾을 수 없습니다."));

                            GroupInvite newInvite = GroupInvite.builder()
                                    .group(group)
                                    .sender(inviter)
                                    .receiver(userRepository.findById(friendUserId).orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다.")))
                                    .requestStatus(RequestStatus.PENDING)
                                    .build();

                            GroupInvite savedInvite = groupInviteRepository.save(newInvite);

                            return GroupInviteResponse.builder()
                                    .requestId(savedInvite.getId())
                                    .friendUserId(friendUserId)
                                    .nickname(receiver.getNickname())
                                    .name(receiver.getName())
                                    .requestStatus(RequestStatus.PENDING.name())
                                    .createdAt(savedInvite.getCreatedAt())
                                    .build();
                        }
                    } else {
                        return GroupInviteResponse.builder()
                                .friendUserId(friendUserId)
                                .nickname("N/A")
                                .name("N/A")
                                .requestStatus("친구 관계가 아닙니다.")
                                .createdAt(LocalDateTime.now())
                                .build();
                    }
                })
                .collect(Collectors.toList());
    }
}