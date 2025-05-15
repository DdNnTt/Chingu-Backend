package com.chingubackend.dto.response;

import com.chingubackend.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MessageResponse {
    private Long messageId;         // 쪽지 ID
    private String sender;          // 송신자 닉네임
    private String receiver;        // 수신자 닉네임
    private String content;         // 내용
    private LocalDateTime sendTime; // 보낸 시간
    private boolean readStatus;     // 읽음 여부

    public static MessageResponse fromEntity(Message entity) {
        return new MessageResponse(
                entity.getMessageId(),
                entity.getSender().getNickname(),
                entity.getReceiver().getNickname(),
                entity.getContent(),
                entity.getSendTime(),
                entity.isReadStatus()
        );
    }
}