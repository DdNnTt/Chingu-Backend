package com.chingubackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageReadResponse {
    private Long messageId;
    private boolean readStatus;
}
