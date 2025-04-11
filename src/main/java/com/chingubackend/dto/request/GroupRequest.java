package com.chingubackend.dto.request;

import jakarta.validation.constraints.NotBlank;

public class GroupRequest {

    @NotBlank(message = "그룹 이름은 비어 있을 수 없습니다.")
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