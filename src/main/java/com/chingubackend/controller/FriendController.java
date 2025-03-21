package com.chingubackend.controller;

import com.chingubackend.dto.request.FriendRequest;
import com.chingubackend.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
