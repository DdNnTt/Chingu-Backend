package com.chingubackend.controller;

import com.chingubackend.dto.request.GroupMemoryRequest;
import com.chingubackend.dto.request.GroupMemoryUpdateRequest;
import com.chingubackend.dto.response.GroupMemoryListResponse;
import com.chingubackend.dto.response.GroupMemoryResponse;
import com.chingubackend.service.GroupMemoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
    @Operation(summary = "추억 앨범 글 작성", description = "그룹 ID를 기반으로 추억 앨범 글을 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "작성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<GroupMemoryResponse> createMemory(
            @PathVariable Long groupId,
            @RequestBody @Valid GroupMemoryRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        GroupMemoryResponse response = groupMemoryService.createGroupMemory(groupId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{memoryId}")
    @Operation(summary = "추억 앨범 글 수정", description = "작성자만 수정할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "요청 오류"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "글 또는 그룹 없음")
    })
    public ResponseEntity<GroupMemoryResponse> updateMemory(
            @PathVariable Long groupId,
            @PathVariable Long memoryId,
            @RequestBody @Valid GroupMemoryUpdateRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        GroupMemoryResponse response = groupMemoryService.updateGroupMemory(groupId, memoryId, userId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{memoryId}")
    @Operation(summary = "추억 앨범 글 삭제", description = "작성자 본인만 삭제 가능합니다.")
    public ResponseEntity<Map<String, Object>> deleteMemory(
            @PathVariable Long groupId,
            @PathVariable Long memoryId,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        groupMemoryService.deleteGroupMemory(groupId, memoryId, userId);

        Map<String, Object> response = Map.of(
                "message", "추억 앨범 글이 성공적으로 삭제되었습니다.",
                "memoryId", memoryId,
                "groupId", groupId
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "그룹 추억 앨범 글 리스트 조회")
    @GetMapping
    public ResponseEntity<List<GroupMemoryListResponse>> getMemories(
            @PathVariable Long groupId,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        List<GroupMemoryListResponse> response = groupMemoryService.getGroupMemories(groupId, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "그룹 추억 앨범 글 상세 조회")
    @GetMapping("/{memoryId}")
    public ResponseEntity<GroupMemoryResponse> getMemoryDetail(
            @PathVariable Long groupId,
            @PathVariable Long memoryId,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        GroupMemoryResponse response = groupMemoryService.getMemoryDetail(groupId, memoryId, userId);
        return ResponseEntity.ok(response);
    }
}