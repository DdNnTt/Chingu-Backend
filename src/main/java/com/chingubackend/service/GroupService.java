package com.chingubackend.service;

import com.chingubackend.dto.request.GroupRequest;
import com.chingubackend.dto.response.GroupDeleteResponse;
import com.chingubackend.dto.response.GroupResponse;
import com.chingubackend.entity.Group;
import com.chingubackend.entity.User;
import com.chingubackend.repository.GroupRepository;
import com.chingubackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public GroupService(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
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
}