package com.chingubackend.service;

import com.chingubackend.dto.request.MessageRequest;
import com.chingubackend.dto.response.MessageReadResponse;
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
    MessageResponse getMessage(Long messageId, UserDetails userDetails); // 조회
    MessageReadResponse markAsRead(Long messageId, UserDetails userDetails) throws Exception; // 읽음 처리

    //특정 유저의 보낸 쪽지 조회
    List<MessageResponse> readAllSentMessages(UserDetails userDetails);
}