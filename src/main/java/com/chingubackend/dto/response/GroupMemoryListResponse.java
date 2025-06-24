package com.chingubackend.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GroupMemoryListResponse {
    private Long memoryId;
    private String imageUrl;
    private String description;
    private LocalDateTime createdAt;
}
