package com.chingubackend.dto.response;

import com.chingubackend.entity.Group;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GroupResponse {
    private Long groupId;
    private String groupName;
    private String description;
    private String creatorNickname;
    private LocalDateTime createdAt;

    public static GroupResponse fromEntity(Group group) {
        String creatorUserId = group.getCreator().getUserId();
        String creatorNickname = creatorUserId.equals("deleted-user")
                ? "탈퇴한 사용자"
                : group.getCreator().getNickname();

        return GroupResponse.builder()
                .groupId(group.getId())
                .groupName(group.getGroupName())
                .description(group.getDescription())
                .creatorNickname(creatorNickname)
                .createdAt(group.getCreatedAt())
                .build();
    }
}