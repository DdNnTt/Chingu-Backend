package com.chingubackend.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GroupMemoryResponse {
    private Long memoryId;
    private Long groupId;
    private String nickname;
    private String title;
    private String content;
    private String imageUrl1;
    private String imageUrl2;
    private String imageUrl3;
    private String location;
    private LocalDate memoryDate;
    private LocalDateTime createdAt;
}
