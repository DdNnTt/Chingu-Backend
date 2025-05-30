package com.chingubackend.dto.request;

import com.chingubackend.entity.Message;
import com.chingubackend.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageRequest {

    @NotBlank(message = "수신자 ID를 입력해 주세요")
    private String receiver; // 쪽지 수신자 ID or 닉네임

    @NotBlank(message = "내용을 입력해 주세요")
    private String content; // 쪽지 내용

    // 송신자와 수신자 유저 엔티티가 필요한 경우에 사용할 수 있도록 변환 메소드 제공
    public Message toEntity(User sender, User receiverEntity) {
        return new Message(
                null, // 메시지 ID는 자동 생성
                sender,
                receiverEntity,
                content,
                null, // 송신 시간은 Entity에서 처리
                false, // 읽음 여부 기본값
                false,                 // senderDeleted
                false                  // receiverDeleted
        );
    }
}