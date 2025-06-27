package com.chingubackend.dto.response;

import com.chingubackend.dto.admin.response.GroupMemberSummaryResponse;
import com.chingubackend.entity.Group;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class AdminGroupResponse {
    private Long groupId;
    private String groupName;
    private LocalDateTime createdDate;
    private List<GroupMemberSummaryResponse> members;

    public AdminGroupResponse(Group group) {
        this.groupId = group.getId();
        this.groupName = group.getGroupName();
        this.createdDate = group.getCreatedAt();
        this.members = group.getMembers().stream()
                .filter(member -> member.getUser() != null)
                .map(member -> GroupMemberSummaryResponse.from(member.getUser()))
                .collect(Collectors.toList());
    }
}