package com.chingubackend.service;

import com.chingubackend.dto.request.GroupRequest;
import com.chingubackend.dto.response.GroupDeleteResponse;
import com.chingubackend.dto.response.GroupResponse;
import com.chingubackend.entity.Group;
import com.chingubackend.repository.GroupRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class GroupService {
    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public GroupResponse createGroup(GroupRequest request) {
        Group group = Group.builder()
                .groupName(request.getGroupName())
                .description(request.getDescription())
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
    public GroupDeleteResponse deleteGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("해당 그룹을 찾을 수 없습니다."));

        groupRepository.delete(group);

        return GroupDeleteResponse.builder()
                .groupId(groupId)
                .deleted(true)
                .build();
    }
}