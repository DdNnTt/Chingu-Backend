package com.chingubackend.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GroupDeleteResponse {
    private Long groupId;
    private boolean deleted;
}
