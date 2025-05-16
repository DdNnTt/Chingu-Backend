package com.chingubackend.controller;

import com.chingubackend.dto.request.MessageRequest;
import com.chingubackend.dto.response.MessageReadResponse;
import com.chingubackend.dto.response.MessageResponse;
import com.chingubackend.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // @AuthenticationPrincipal 사용 시 Authentication 객체의 getPrincipal() 메소드를 통해 반환되는 객체를 받는다.
    @PostMapping("/send") // 쪽지 전송
    public ResponseEntity<MessageResponse> sendMessage(
            @RequestBody MessageRequest messageRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        MessageResponse response = messageService.sendMessage(messageRequest, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/read/all") // nickname의 모든 쪽지 조회
    public ResponseEntity<List<MessageResponse>> readAllMessages(@AuthenticationPrincipal UserDetails userDetails) {
        List<MessageResponse> messages = messageService.readAllMessages(userDetails.getUsername(), userDetails);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/read/{messageId}") // 특정 쪽지 조회
    public ResponseEntity<MessageResponse> getMessage(@PathVariable Long messageId, @AuthenticationPrincipal UserDetails userDetails) {
        MessageResponse message = messageService.getMessage(messageId, userDetails);
        return ResponseEntity.ok(message);
    }

    @PatchMapping("/read/{messageId}")
    public ResponseEntity<MessageReadResponse> markMessageAsRead(
            @PathVariable Long messageId,
            @AuthenticationPrincipal UserDetails userDetails) throws Exception {

        MessageReadResponse response = messageService.markAsRead(messageId, userDetails);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sent")
    public ResponseEntity<List<MessageResponse>> readAllSentMessages(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<MessageResponse> messages = messageService.readAllSentMessages(userDetails);
        return ResponseEntity.ok(messages);
    }
}