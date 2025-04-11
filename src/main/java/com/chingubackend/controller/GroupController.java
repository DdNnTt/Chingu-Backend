package com.chingubackend.controller;

import com.chingubackend.dto.request.GroupRequest;
import com.chingubackend.dto.response.GroupDeleteResponse;
import com.chingubackend.dto.response.GroupResponse;
import com.chingubackend.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @Valid @RequestBody GroupRequest request,
            HttpServletRequest httpRequest) {

        GroupResponse response = groupService.createGroup(request, httpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "그룹 삭제",
            description = "그룹 ID에 해당하는 그룹을 삭제합니다. (그룹 생성자만 삭제 가능)"
    )
    @DeleteMapping("/{groupId}")
    public ResponseEntity<GroupDeleteResponse> deleteGroup(
            @PathVariable Long groupId,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        GroupDeleteResponse response = groupService.deleteGroup(groupId, userId);
        return ResponseEntity.ok(response);
    }
}