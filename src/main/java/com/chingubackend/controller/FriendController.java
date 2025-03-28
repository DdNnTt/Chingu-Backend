package com.chingubackend.controller;

import com.chingubackend.dto.request.FriendRequest;
import com.chingubackend.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;

    @PostMapping("/request")
    public ResponseEntity<String> sendFriendRequest(@RequestBody FriendRequest dto){
        String resultMessage = friendService.sendFriendRequest(dto);
        return ResponseEntity.ok(resultMessage);
    }

    @GetMapping("/requests")
    public ResponseEntity<List<FriendRequest.PendingRequest>> getReceivedRequests(@RequestParam Long userId) {
        return ResponseEntity.ok(friendService.getReceivedFriendRequests(userId));
    }

    @PutMapping("/respond")
    public ResponseEntity<String> respondToFriendRequest(@RequestBody FriendRequest.ResponseRequest dto) {
        String result = friendService.respondToFriendRequest(dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<FriendRequest.FriendList>> getAcceptedFriends(@RequestParam Long userId) {
        List<FriendRequest.FriendList> friends = friendService.getAcceptedFriends(userId);
        return ResponseEntity.ok(friends);
    }

    @DeleteMapping("/{friendUserId}")
    public ResponseEntity<Map<String, Object>> deleteFriend(
            @PathVariable Long friendUserId,
            @RequestParam Long userId // 인증 붙으면 @AuthenticationPrincipal 로 대체
    ) {
        Map<String, Object> result = friendService.deleteFriend(userId, friendUserId);
        return ResponseEntity.ok(result);
    }


}
