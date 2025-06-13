package com.chingubackend.service;

import com.chingubackend.config.WebSocketMessageHandler;
import com.chingubackend.dto.request.MessageRequest;
import com.chingubackend.dto.response.MessageReadResponse;
import com.chingubackend.dto.response.MessageResponse;
import com.chingubackend.entity.Message;
import com.chingubackend.entity.User;
import com.chingubackend.exception.ForbiddenException;
import com.chingubackend.exception.NotFoundException;
import com.chingubackend.exception.SuccessResponse;
import com.chingubackend.repository.FriendRepository;
import com.chingubackend.repository.MessageRepository;
import com.chingubackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final WebSocketMessageHandler webSocketMessageHandler;
    private final FriendRepository friendRepository;

    public MessageResponse sendMessage(MessageRequest messageRequest, UserDetails userDetails) {
        String senderUserId = userDetails.getUsername();

        log.info("JWT 인증이 완료되었습니다. sender = {}", senderUserId);

        User sender = userRepository.findByUserId(senderUserId)
                .orElseThrow(() -> new NotFoundException("보내는 사용자를 찾을 수 없습니다."));

        User receiver = userRepository.findByNickname(messageRequest.getReceiver())
                .orElseThrow(() -> new NotFoundException("수신자를 찾을 수 없습니다."));

        boolean isFriend = friendRepository.existsFriendship(sender.getId(), receiver.getId())
                || friendRepository.existsFriendship(receiver.getId(), sender.getId());

        if (!isFriend) {
            throw new ForbiddenException("쪽지는 친구에게만 보낼 수 있습니다.");
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

    public List<MessageResponse> readAllMessages(String receiverUserId, UserDetails userDetails) {
        if (!userDetails.getUsername().equals(receiverUserId)) {
            throw new ForbiddenException("쪽지를 조회할 권한이 없습니다.");
        }

        User receiver = userRepository.findByUserId(receiverUserId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        return messageRepository.findAllByReceiverOrderBySendTimeDesc(receiver)
                .stream()
                .map(MessageResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public MessageResponse getMessage(Long messageId, UserDetails userDetails) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("쪽지를 찾을 수 없습니다."));

        if (!message.getReceiver().getUserId().equals(userDetails.getUsername())) {
            throw new ForbiddenException("해당 쪽지에 대한 접근 권한이 없습니다.");
        }

        return MessageResponse.fromEntity(message);
    }

    public MessageReadResponse markAsRead(Long messageId, UserDetails userDetails) throws Exception {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("쪽지를 찾을 수 없습니다."));

        if (!message.getReceiver().getUserId().equals(userDetails.getUsername())) {
            throw new ForbiddenException("해당 쪽지를 읽을 권한이 없습니다.");
        }

        boolean wasAlreadyRead = message.isReadStatus();

        if (!wasAlreadyRead) {
            message.setReadStatus(true);
            messageRepository.save(message);

            long unreadCount = messageRepository.countByReceiverIdAndReadStatus(message.getReceiver().getId(), false);
            try {
                webSocketMessageHandler.sendNotification(
                        message.getReceiver().getUserId(),
                        "읽지 않은 쪽지 수: " + unreadCount
                );
            } catch (Exception e) {
                log.warn("웹소켓 알림 실패: {}", e.getMessage());
            }
        }

        return new MessageReadResponse(messageId, true);
    }

    public List<MessageResponse> readAllSentMessages(UserDetails userDetails) {
        User sender = userRepository.findByUserId(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        return messageRepository.findAllBySenderOrderBySendTimeDesc(sender)
                .stream()
                .map(MessageResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public SuccessResponse deleteMessage(Long messageId, UserDetails userDetails) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("쪽지를 찾을 수 없습니다."));

        // 삭제 권한 확인
        String requesterId = userDetails.getUsername();
        boolean isSender = message.getSender().getUserId().equals(requesterId);
        boolean isReceiver = message.getReceiver().getUserId().equals(requesterId);

        if (!isSender && !isReceiver) {
            throw new ForbiddenException("해당 쪽지를 삭제할 권한이 없습니다.");
        }

        if (isSender){
            message.setSenderDeleted(true);
        }
        if (isReceiver){
            message.setReceiverDeleted(true);
        }

//        // 둘 다 삭제했을 경우 완전 삭제
//        if (message.isSenderDeleted() && message.isReceiverDeleted()) {
//            messageRepository.delete(message);
//        } else {
//            messageRepository.save(message);
//        }
        messageRepository.save(message);

        return SuccessResponse.of("쪽지가 삭제 처리되었습니다.");
    }

}
