package com.chingubackend.controller;

import com.chingubackend.dto.request.GroupRequest;
import com.chingubackend.dto.response.GroupResponse;
import com.chingubackend.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @Operation(
            summary = "그룹 생성",
            description = "새로운 그룹을 생성합니다."
    )
    @PostMapping("/create")
    public ResponseEntity<GroupResponse> createGroup(
            @Valid @RequestBody GroupRequest request) {

        GroupResponse response = groupService.createGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}