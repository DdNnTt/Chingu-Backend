package com.chingubackend.controller;

import com.chingubackend.dto.request.GroupInviteRequest;
import com.chingubackend.dto.request.GroupRequest;
import com.chingubackend.dto.response.GroupDeleteResponse;
import com.chingubackend.dto.response.GroupInviteResponse;
import com.chingubackend.dto.response.GroupResponse;
import com.chingubackend.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
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

    @Operation(
            summary = "그룹 친구 초대",
            description = "친구 관계인 사용자들에게 그룹 초대 요청을 보냅니다."
    )
    @PostMapping("/{groupId}/invite")
    public ResponseEntity<List<GroupInviteResponse>> inviteFriendsToGroup(
            @PathVariable Long groupId,
            @Valid @RequestBody GroupInviteRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        List<GroupInviteResponse> response = groupService.inviteFriendsToGroup(groupId, userId, request.getFriendUserIds());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "초대 받은 그룹 목록 조회",
            description = "로그인한 사용자가 초대 받은 그룹 목록을 조회합니다."
    )
    @GetMapping("/invites")
    public ResponseEntity<List<GroupInviteResponse>> getReceivedInvites(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<GroupInviteResponse> response = groupService.getReceivedInvites(userId);

        return ResponseEntity.ok(response);
    }
}