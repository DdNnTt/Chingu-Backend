package com.chingubackend.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GroupResponse {
    private Long groupId;
    private String groupName;
    private String description;
    private LocalDateTime createdAt;
}