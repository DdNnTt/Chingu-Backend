package com.chingubackend.controller;

import com.chingubackend.dto.request.FriendRequest;
import com.chingubackend.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<FriendRequest.PendingRequestDto>> getReceivedRequests(@RequestParam Long userId) {
        return ResponseEntity.ok(friendService.getReceivedFriendRequests(userId));
    }

    @PutMapping("/respond")
    public ResponseEntity<String> respondToFriendRequest(@RequestBody FriendRequest.ResponseRequestDto dto) {
        String result = friendService.respondToFriendRequest(dto);
        return ResponseEntity.ok(result);
    }


}
