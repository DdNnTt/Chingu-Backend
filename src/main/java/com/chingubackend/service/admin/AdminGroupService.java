package com.chingubackend.service.admin;

import com.chingubackend.entity.Group;
import com.chingubackend.exception.GroupNotFoundException;
import com.chingubackend.repository.GroupInviteRepository;
import com.chingubackend.repository.GroupMemberRepository;
import com.chingubackend.repository.GroupMemoryRepository;
import com.chingubackend.repository.GroupRepository;
import com.chingubackend.repository.GroupScheduleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminGroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupInviteRepository groupInviteRepository;
    private final GroupScheduleRepository groupScheduleRepository;
    private final GroupMemoryRepository groupMemoryRepository;

    @Transactional
    public void deleteGroupByAdmin(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("해당 그룹을 찾을 수 없습니다."));

        groupScheduleRepository.deleteByGroup(group);
        groupMemberRepository.deleteByGroup(group);
        groupInviteRepository.deleteByGroup(group);
        groupMemoryRepository.deleteByGroup(group);

        groupRepository.delete(group);
    }
}