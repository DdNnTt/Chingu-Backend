package com.chingubackend.dto.request;

public class GroupRequest {
    private String groupName;
    private String description;

    public String getGroupName() {
        return groupName;
    }

    public String getDescription() {
        return description;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
