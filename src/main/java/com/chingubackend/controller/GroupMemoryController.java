package com.chingubackend.controller;

import com.chingubackend.dto.request.GroupMemoryRequest;
import com.chingubackend.dto.response.GroupMemoryResponse;
import com.chingubackend.service.GroupMemoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/groups/{groupId}/albums")
@RequiredArgsConstructor
public class GroupMemoryController {

    private final GroupMemoryService groupMemoryService;

    @PostMapping
    public ResponseEntity<GroupMemoryResponse> createMemory(
            @PathVariable Long groupId,
            @RequestBody @Valid GroupMemoryRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        GroupMemoryResponse response = groupMemoryService.createGroupMemory(groupId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}