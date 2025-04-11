package com.chingubackend.service;

import com.chingubackend.dto.request.GroupRequest;
import com.chingubackend.dto.response.GroupResponse;
import com.chingubackend.entity.Group;
import com.chingubackend.repository.GroupRepository;
import org.springframework.stereotype.Service;

@Service
public class GroupService {
    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public GroupResponse createGroup(GroupRequest request) {
        Group group = new Group();
        group.setGroupName(request.getGroupName());
        group.setDescription(request.getDescription());

        Group saved = groupRepository.save(group);

        return new GroupResponse(
                saved.getId(),
                saved.getGroupName(),
                saved.getDescription(),
                saved.getCreatedAt()
        );
    }
}
