package com.chingubackend.service;

import com.chingubackend.dto.request.MessageRequest;
import com.chingubackend.dto.response.MessageResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MessageService {
    // 쪽지 전송
    MessageResponse sendMessage(MessageRequest messageRequest, UserDetails userDetails);

    // 특정 유저(Nickname)의 모든 쪽지 조회
    List<MessageResponse> readAllMessages(String nickname, UserDetails userDetails);

    // 특정 메시지 ID로 단일 쪽지 조회
    MessageResponse readMessage(Long messageId, UserDetails userDetails);
}