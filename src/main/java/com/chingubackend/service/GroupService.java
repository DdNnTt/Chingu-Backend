package com.chingubackend.service;

import com.chingubackend.dto.request.GroupInviteRequest;
import com.chingubackend.dto.request.GroupRequest;
import com.chingubackend.dto.response.GroupDeleteResponse;
import com.chingubackend.dto.response.GroupInviteResponse;
import com.chingubackend.dto.response.GroupResponse;
import com.chingubackend.entity.Friend;
import com.chingubackend.entity.Group;
import com.chingubackend.entity.User;
import com.chingubackend.repository.FriendRepository;
import com.chingubackend.repository.GroupRepository;
import com.chingubackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    public GroupService(GroupRepository groupRepository, UserRepository userRepository, FriendRepository friendRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
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

        // 친구 관계를 확인하고 초대 요청을 생성
        List<GroupInviteResponse> responses = friendUserIds.stream()
                .map(friendUserId -> {
                    // 두 사용자 간의 친구 관계가 ACCEPTED 상태인지 확인
                    Optional<Friend> acceptedFriend = friendRepository.findAcceptedFriend(userId, friendUserId);
                    if (acceptedFriend.isPresent()) {
                        // 친구 관계가 있으면 초대 요청을 생성
                        return GroupInviteResponse.builder()
                                .requestId(acceptedFriend.get().getId()) // 초대 요청 ID 설정
                                .friendUserId(friendUserId)
                                .nickname("친구닉네임") // 실제 닉네임을 여기서 설정해야 합니다.
                                .name("친구이름") // 실제 이름을 여기서 설정해야 합니다.
                                .requestStatus("PENDING") // 초기 상태는 PENDING
                                .createdAt(LocalDateTime.now())
                                .build();
                    } else {
                        throw new IllegalArgumentException("친구 관계가 아니므로 초대할 수 없습니다.");
                    }
                })
                .collect(Collectors.toList());

        return responses;
    }
}