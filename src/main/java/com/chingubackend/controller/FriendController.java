package com.chingubackend.controller;

import com.chingubackend.dto.request.FriendRequest;
import com.chingubackend.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "FRIEND API", description = "친구 관련 API")
@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;

    @Operation(summary = "친구 요청 전송", description = "다른 사용자에게 친구 요청을 전송합니다.")
    @PostMapping("/request")
    public ResponseEntity<String> sendFriendRequest(@RequestBody FriendRequest dto){
        String resultMessage = friendService.sendFriendRequest(dto);
        return ResponseEntity.ok(resultMessage);
    }

    @Operation(summary = "받은 친구 요청 목록", description = "로그인한 사용자가 받은 친구 요청 목록을 조회합니다.")
    @GetMapping("/requests")
    public ResponseEntity<List<FriendRequest.PendingRequest>> getReceivedRequests(
            @Parameter(description = "로그인한 사용자 ID", example = "1") @RequestParam Long userId) {
        return ResponseEntity.ok(friendService.getReceivedFriendRequests(userId));
    }

    @Operation(summary = "친구 요청 응답", description = "수신한 친구 요청에 수락 또는 거절로 응답합니다.")
    @PutMapping("/respond")
    public ResponseEntity<String> respondToFriendRequest(@RequestBody FriendRequest.ResponseRequest dto) {
        String result = friendService.respondToFriendRequest(dto);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "친구 목록 조회", description = "수락된 친구 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<FriendRequest.FriendList>> getAcceptedFriends(
            @Parameter(description = "로그인한 사용자 ID", example = "1") @RequestParam Long userId) {
        List<FriendRequest.FriendList> friends = friendService.getAcceptedFriends(userId);
        return ResponseEntity.ok(friends);
    }

    @Operation(summary = "친구 삭제", description = "기존 친구를 목록에서 삭제합니다.")
    @DeleteMapping("/{friendUserId}")
    public ResponseEntity<Map<String, Object>> deleteFriend(
            @Parameter(description = "삭제할 친구의 사용자 ID", example = "2") @PathVariable Long friendUserId,
            @Parameter(description = "로그인한 사용자 ID", example = "1") @RequestParam Long userId // 인증 붙으면 @AuthenticationPrincipal 로 대체
    ) {
        Map<String, Object> result = friendService.deleteFriend(userId, friendUserId);
        return ResponseEntity.ok(result);
    }


}
