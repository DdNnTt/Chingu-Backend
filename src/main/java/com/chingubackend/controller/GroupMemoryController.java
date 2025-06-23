package com.chingubackend.controller;

import com.chingubackend.dto.request.GroupMemoryRequest;
import com.chingubackend.dto.response.GroupMemoryResponse;
import com.chingubackend.service.GroupMemoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
}