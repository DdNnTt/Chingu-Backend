package com.chingubackend.service;

import com.chingubackend.config.WebSocketMessageHandler;
import com.chingubackend.dto.request.MessageRequest;
import com.chingubackend.dto.response.MessageResponse;
import com.chingubackend.entity.Message;
import com.chingubackend.entity.User;
import com.chingubackend.repository.FriendRepository;
import com.chingubackend.repository.MessageRepository;
import com.chingubackend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final WebSocketMessageHandler webSocketMessageHandler;
    private final FriendRepository friendRepository;

    public MessageServiceImpl(MessageRepository messageRepository,
                              UserRepository userRepository,
                              WebSocketMessageHandler webSocketMessageHandler, FriendRepository friendRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.webSocketMessageHandler = webSocketMessageHandler;
        this.friendRepository = friendRepository;
    }

    @Override
    public MessageResponse sendMessage(MessageRequest messageRequest, UserDetails userDetails) {
        String senderUserId = userDetails.getUsername(); // JWT에서 "sub" = userId

        log.info("JWT 인증이 완료되었습니다. sender = {}", senderUserId);

        User sender = userRepository.findByUserId(senderUserId)
                .orElseThrow(() -> new IllegalArgumentException("보내는 분이 존재하지 않습니다."));

        User receiver = userRepository.findByNickname(messageRequest.getReceiver())
                .orElseThrow(() -> new IllegalArgumentException("받는 분이 존재하지 않습니다."));

        // ✅ 친구 관계 확인
        boolean isFriend = friendRepository.existsFriendship(sender.getId(), receiver.getId())
                || friendRepository.existsFriendship(receiver.getId(), sender.getId());

        if (!isFriend) {
            throw new IllegalStateException("쪽지는 친구에게만 보낼 수 있습니다.");
        }

        Message messageEntity = messageRequest.toEntity(sender, receiver);
        messageRepository.save(messageEntity);

        try {
            long unreadCount = messageRepository.countByReceiverIdAndReadStatus(receiver.getId(), false);
            webSocketMessageHandler.sendNotification(receiver.getUserId(),
                    "새로운 쪽지가 도착했습니다. 읽지 않은 쪽지 수: " + unreadCount);
        } catch (Exception e) {
            log.warn("웹소켓 알림 전송 실패: {}", e.getMessage());
        }

        return MessageResponse.fromEntity(messageEntity);
    }


    @Override
    public List<MessageResponse> readAllMessages(String senderUserId, UserDetails userDetails) {
        if (!userDetails.getUsername().equals(senderUserId)) {
            throw new AccessDeniedException("인증된 사용자가 아닙니다.");
        }

        User receiver = userRepository.findByUserId(senderUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        return messageRepository.findAllByReceiverOrderBySendTimeDesc(receiver)
                .stream()
                .map(MessageResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public MessageResponse readMessage(Long messageId, UserDetails userDetails) {

        log.info("(MessageService) readMessage 실행: messageId = {}", messageId);

        Message messageEntity = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("해당 쪽지가 존재하지 않습니다."));

        if (!messageEntity.getReceiver().getUserId().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("쪽지를 읽을 권한이 없습니다.");
        }

        messageEntity.setReadStatus(true); // 읽음 처리
        messageRepository.save(messageEntity);

        return MessageResponse.fromEntity(messageEntity);
    }
}
