package com.chingubackend.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GroupDetailResponse {
    private Long groupId;
    private String groupName;
    private String description;
    private LocalDateTime createdAt;
    private List<GroupAlbumSummary> groupMemories;

    @Getter
    @Builder
    public static class GroupAlbumSummary {
        private Long albumId;
        private String albumTitle;
        private String albumImage;
        private LocalDateTime createdAt;
    }
}
