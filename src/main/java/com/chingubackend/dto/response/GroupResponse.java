package com.chingubackend.dto.response;

import java.time.LocalDateTime;

public class GroupResponse {
    private Long groupId;
    private String groupName;
    private String description;
    private LocalDateTime createdAt;

    public GroupResponse(Long groupId, String groupName, String description, LocalDateTime createdAt) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.description = description;
        this.createdAt = createdAt;
    }
    public Long getGroupId() { return groupId; }
    public String getGroupName() { return groupName; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }

}
