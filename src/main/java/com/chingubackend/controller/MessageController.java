package com.chingubackend.controller;

import com.chingubackend.dto.request.MessageRequest;
import com.chingubackend.dto.response.MessageReadResponse;
import com.chingubackend.dto.response.MessageResponse;
import com.chingubackend.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "MESSAGE API", description = "쪽지 관련 API")
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @Operation(summary = "쪽지 전송", description = "친구에게 쪽지를 전송합니다.")
    @PostMapping("/send")
    public ResponseEntity<MessageResponse> sendMessage(
            @RequestBody MessageRequest messageRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        MessageResponse response = messageService.sendMessage(messageRequest, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "받은 쪽지 전체 조회", description = "로그인한 사용자가 받은 모든 쪽지를 조회합니다.")
    @GetMapping("/read/all")
    public ResponseEntity<List<MessageResponse>> readAllMessages(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<MessageResponse> messages = messageService.readAllMessages(userDetails.getUsername(), userDetails);
        return ResponseEntity.ok(messages);
    }

    @Operation(summary = "쪽지 단일 조회", description = "쪽지 ID로 특정 쪽지를 조회합니다.")
    @GetMapping("/read/{messageId}")
    public ResponseEntity<MessageResponse> getMessage(
            @Parameter(description = "조회할 쪽지 ID", example = "1") @PathVariable Long messageId,
            @AuthenticationPrincipal UserDetails userDetails) {

        MessageResponse message = messageService.getMessage(messageId, userDetails);
        return ResponseEntity.ok(message);
    }

    @Operation(summary = "쪽지 읽음 처리", description = "특정 쪽지를 읽음 처리합니다.")
    @PatchMapping("/read/{messageId}")
    public ResponseEntity<MessageReadResponse> markMessageAsRead(
            @Parameter(description = "읽음 처리할 쪽지 ID", example = "1") @PathVariable Long messageId,
            @AuthenticationPrincipal UserDetails userDetails) throws Exception {

        MessageReadResponse response = messageService.markAsRead(messageId, userDetails);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "보낸 쪽지 전체 조회", description = "로그인한 사용자가 보낸 모든 쪽지를 조회합니다.")
    @GetMapping("/sent")
    public ResponseEntity<List<MessageResponse>> readAllSentMessages(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<MessageResponse> messages = messageService.readAllSentMessages(userDetails);
        return ResponseEntity.ok(messages);
    }

    @Operation(summary = "쪽지 삭제", description = "쪽지를 삭제합니다. 보낸 사람 또는 받은 사람만 삭제할 수 있습니다.")
    @DeleteMapping("/{messageId}")
    public ResponseEntity<MessageResponse> deleteMessage(
            @Parameter(description = "삭제할 쪽지 ID", example = "1") @PathVariable Long messageId,
            @AuthenticationPrincipal UserDetails userDetails) {

        MessageResponse response = messageService.deleteMessage(messageId, userDetails);
        return ResponseEntity.ok(response);
    }
}
