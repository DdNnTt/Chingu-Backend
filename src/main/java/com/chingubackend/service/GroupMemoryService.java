package com.chingubackend.service;

import com.chingubackend.dto.request.GroupMemoryRequest;
import com.chingubackend.dto.response.GroupMemoryResponse;
import com.chingubackend.entity.Group;
import com.chingubackend.entity.GroupMemory;
import com.chingubackend.entity.User;
import com.chingubackend.repository.GroupMemoryRepository;
import com.chingubackend.repository.GroupRepository;
import com.chingubackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupMemoryService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemoryRepository groupMemoryRepository;

    @Transactional
    public GroupMemoryResponse createGroupMemory(Long groupId, Long userId, GroupMemoryRequest request) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("그룹이 존재하지 않습니다."));

        User user = userRepository.findById(userId)
                .orElseGet(() -> userRepository.findByUserId("system")
                        .orElseThrow(() -> new IllegalStateException("시스템 사용자 정보가 없습니다.")));

        GroupMemory memory = GroupMemory.builder()
                .group(group)
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrl1(request.getImageUrl1())
                .imageUrl2(request.getImageUrl2())
                .imageUrl3(request.getImageUrl3())
                .location(request.getLocation())
                .memoryDate(request.getMemoryDate())
                .build();

        GroupMemory saved = groupMemoryRepository.save(memory);

        return GroupMemoryResponse.builder()
                .memoryId(saved.getId())
                .groupId(groupId)
                .nickname(user.getNickname())
                .title(saved.getTitle())
                .content(saved.getContent())
                .imageUrl1(saved.getImageUrl1())
                .imageUrl2(saved.getImageUrl2())
                .imageUrl3(saved.getImageUrl3())
                .location(saved.getLocation())
                .memoryDate(saved.getMemoryDate())
                .createdAt(saved.getCreatedDate())
                .build();
    }
}