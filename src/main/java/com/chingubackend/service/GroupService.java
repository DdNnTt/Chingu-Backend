package com.chingubackend.service;

import com.chingubackend.dto.request.GroupRequest;
import com.chingubackend.dto.response.GroupDeleteResponse;
import com.chingubackend.dto.response.GroupDetailResponse;
import com.chingubackend.dto.response.GroupInviteResponse;
import com.chingubackend.dto.response.GroupResponse;
import com.chingubackend.entity.Friend;
import com.chingubackend.entity.Group;
import com.chingubackend.entity.GroupInvite;
import com.chingubackend.entity.GroupMember;
import com.chingubackend.entity.GroupMemory;
import com.chingubackend.entity.User;
import com.chingubackend.model.MemberStatus;
import com.chingubackend.model.RequestStatus;
import com.chingubackend.repository.FriendRepository;
import com.chingubackend.repository.GroupInviteRepository;
import com.chingubackend.repository.GroupMemberRepository;
import com.chingubackend.repository.GroupMemoryRepository;
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
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMemoryRepository groupMemoryRepository;

    public GroupService(GroupRepository groupRepository,
                        UserRepository userRepository,
                        FriendRepository friendRepository,
                        GroupInviteRepository groupInviteRepository,
                        GroupMemberRepository groupMemberRepository,
                        GroupMemoryRepository groupMemoryRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
        this.groupInviteRepository = groupInviteRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.groupMemoryRepository = groupMemoryRepository;
    }

    @Transactional
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

        GroupMember member = GroupMember.builder()
                .group(saved)
                .user(user)
                .status(MemberStatus.APPROVED)
                .build();
        groupMemberRepository.save(member);

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

    @Transactional
    public List<GroupInviteResponse> inviteFriendsToGroup(Long groupId, Long userId, List<Long> friendUserIds) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("그룹을 찾을 수 없습니다."));

        User inviter = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        List<GroupInvite> groupInvites = groupInviteRepository.findAllByGroupIdWithGroup(groupId);

        return friendUserIds.stream()
                .map(friendUserId -> {
                    Optional<Friend> acceptedFriend = friendRepository.findAcceptedFriend(userId, friendUserId);
                    if (acceptedFriend.isPresent()) {
                        Optional<GroupInvite> existingInvite = groupInvites.stream()
                                .filter(invite -> invite.getReceiver().getId().equals(friendUserId))
                                .findFirst();

                        if (existingInvite.isPresent()) {
                            return GroupInviteResponse.builder()
                                    .requestId(existingInvite.get().getId())
                                    .friendUserId(friendUserId)
                                    .nickname(existingInvite.get().getReceiver().getNickname())
                                    .name(existingInvite.get().getReceiver().getName())
                                    .requestStatus(existingInvite.get().getRequestStatus().name())
                                    .createdAt(existingInvite.get().getCreatedAt())
                                    .groupId(existingInvite.get().getGroup().getId())
                                    .build();
                        } else {
                            User receiver = userRepository.findById(friendUserId)
                                    .orElseThrow(() -> new EntityNotFoundException("초대 대상 사용자를 찾을 수 없습니다."));

                            GroupInvite newInvite = GroupInvite.builder()
                                    .group(group)
                                    .sender(inviter)
                                    .receiver(receiver)
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
                                    .groupId(savedInvite.getGroup().getId())
                                    .build();
                        }
                    } else {
                        return GroupInviteResponse.builder()
                                .friendUserId(friendUserId)
                                .nickname("N/A")
                                .name("N/A")
                                .requestStatus("친구 관계가 아닙니다.")
                                .createdAt(LocalDateTime.now())
                                .groupId(null)
                                .build();
                    }
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<GroupInviteResponse> getReceivedInvites(Long userId) {
        List<GroupInvite> invites = groupInviteRepository.findByReceiverIdWithGroup(userId);

        return invites.stream()
                .map(invite -> GroupInviteResponse.builder()
                        .requestId(invite.getId())
                        .nickname(invite.getSender().getNickname())
                        .name(invite.getSender().getName())
                        .requestStatus(invite.getRequestStatus().name())
                        .createdAt(invite.getCreatedAt())
                        .groupId(invite.getGroup().getId())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public GroupInviteResponse.GroupInviteResponseWithoutFriend respondToInvite(Long requestId, Long userId, RequestStatus status) {
        GroupInvite groupInvite = groupInviteRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("해당 초대 요청을 찾을 수 없습니다."));

        if (!groupInvite.getReceiver().getId().equals(userId)) {
            throw new AccessDeniedException("이 초대는 해당 사용자만 처리할 수 있습니다.");
        }

        if (groupInvite.getGroup() == null) {
            throw new IllegalStateException("그룹 정보가 없습니다.");
        }

        Long groupId = groupInvite.getGroup().getId();

        if (status == RequestStatus.ACCEPTED) {
            groupInvite.updateStatus(RequestStatus.ACCEPTED);

            Optional<GroupMember> existingMember = groupMemberRepository.findByGroupIdAndUserId(groupInvite.getGroup().getId(), userId);
            if (existingMember.isPresent()) {
                throw new IllegalArgumentException("이미 그룹에 가입된 사용자입니다.");
            }

            GroupMember groupMember = GroupMember.builder()
                    .group(groupInvite.getGroup())
                    .user(groupInvite.getReceiver())
                    .status(MemberStatus.APPROVED)
                    .build();

            groupMemberRepository.save(groupMember);
        } else if (status == RequestStatus.REJECTED) {
            groupInvite.updateStatus(RequestStatus.REJECTED);
        }

        groupInviteRepository.save(groupInvite);

        return GroupInviteResponse.builder()
                .requestId(groupInvite.getId())
                .friendUserId(groupInvite.getReceiver().getId())
                .nickname(groupInvite.getReceiver().getNickname())
                .name(groupInvite.getReceiver().getName())
                .requestStatus(groupInvite.getRequestStatus().name())
                .createdAt(groupInvite.getCreatedAt())
                .groupId(groupId)
                .build()
                .toResponseWithoutFriend();
    }

    @Transactional
    public List<GroupResponse> getMyGroups(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 내가 생성한 그룹
        List<Group> createdGroups = groupRepository.findByCreatorId(userId);

        // 내가 초대를 수락한 그룹 (group_members 테이블 기준)
        List<GroupMember> memberGroups = groupMemberRepository.findByUserIdAndStatus(userId, MemberStatus.APPROVED);

        List<Group> joinedGroups = memberGroups.stream()
                .map(GroupMember::getGroup)
                .toList();

        // 중복 제거 (예: 생성자이면서 구성원일 수 있으므로)
        joinedGroups.forEach(group -> {
            if (!createdGroups.contains(group)) {
                createdGroups.add(group);
            }
        });

        return createdGroups.stream()
                .map(group -> GroupResponse.builder()
                        .groupId(group.getId())
                        .groupName(group.getGroupName())
                        .description(group.getDescription())
                        .createdAt(group.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public List<GroupInviteResponse> getGroupInvites(Long groupId) {
        // 그룹 존재 여부 체크
        groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("해당 그룹이 존재하지 않습니다."));

        List<GroupInvite> invites = groupInviteRepository.findAllByGroupIdWithGroup(groupId);

        return invites.stream()
                .map(invite -> GroupInviteResponse.builder()
                        .requestId(invite.getId())
                        .friendUserId(invite.getReceiver().getId())
                        .nickname(invite.getReceiver().getNickname())
                        .name(invite.getReceiver().getName())
                        .requestStatus(invite.getRequestStatus().name())
                        .createdAt(invite.getCreatedAt())
                        .groupId(invite.getGroup().getId())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public GroupDetailResponse getGroupDetail(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("그룹이 존재하지 않습니다."));

        boolean isMember = groupMemberRepository.existsByGroupIdAndUserId(groupId, userId);
        if (!isMember) {
            throw new AccessDeniedException("해당 그룹에 속한 사용자만 조회할 수 있습니다.");
        }

        List<GroupMemory> memories = groupMemoryRepository.findByGroupIdOrderByCreatedDateDesc(groupId);

        List<GroupDetailResponse.GroupAlbumSummary> memoryResponses = memories.stream()
                .map(memory -> GroupDetailResponse.GroupAlbumSummary.builder()
                        .albumId(memory.getId())
                        .albumTitle(memory.getTitle())
                        .albumImage(memory.getImageUrl1())
                        .createdAt(memory.getCreatedDate())
                        .build())
                .toList();

        return GroupDetailResponse.builder()
                .groupId(group.getId())
                .groupName(group.getGroupName())
                .description(group.getDescription())
                .createdAt(group.getCreatedAt())
                .groupMemories(memoryResponses)
                .build();
    }

}