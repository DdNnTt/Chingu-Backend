package com.chingubackend.service;

import com.chingubackend.dto.request.GroupMemoryRequest;
import com.chingubackend.dto.request.GroupMemoryUpdateRequest;
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
import org.springframework.security.access.AccessDeniedException;
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

    @Transactional
    public GroupMemoryResponse updateGroupMemory(Long groupId, Long memoryId, Long userId, GroupMemoryUpdateRequest request) {
        GroupMemory memory = groupMemoryRepository.findById(memoryId)
                .orElseThrow(() -> new EntityNotFoundException("앨범 글을 찾을 수 없습니다."));

        if (!memory.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
        }

        memory.update(request);

        return GroupMemoryResponse.builder()
                .memoryId(memory.getId())
                .groupId(groupId)
                .nickname(memory.getUser().getNickname())
                .title(memory.getTitle())
                .content(memory.getContent())
                .imageUrl1(memory.getImageUrl1())
                .imageUrl2(memory.getImageUrl2())
                .imageUrl3(memory.getImageUrl3())
                .location(memory.getLocation())
                .memoryDate(memory.getMemoryDate())
                .createdAt(memory.getCreatedDate())
                .build();
    }

    @Transactional
    public void deleteGroupMemory(Long groupId, Long memoryId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("그룹이 존재하지 않습니다."));

        GroupMemory memory = groupMemoryRepository.findById(memoryId)
                .orElseThrow(() -> new EntityNotFoundException("앨범 글이 존재하지 않습니다."));

        if (!memory.getGroup().getId().equals(group.getId())) {
            throw new IllegalArgumentException("그룹 정보가 일치하지 않습니다.");
        }

        if (!memory.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        groupMemoryRepository.delete(memory);
    }

}