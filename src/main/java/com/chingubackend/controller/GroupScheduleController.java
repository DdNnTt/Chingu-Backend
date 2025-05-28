package com.chingubackend.controller;

import com.chingubackend.dto.request.GroupScheduleRequest;
import com.chingubackend.dto.response.GroupScheduleResponse;
import com.chingubackend.entity.GroupSchedule;
import com.chingubackend.service.GroupScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups/{groupId}/schedules")
@RequiredArgsConstructor
public class GroupScheduleController {

    private final GroupScheduleService groupScheduleService;

    @PostMapping
    @Operation(summary = "그룹 캘린더 일정 추가", description = "로그인한 사용자가 특정 그룹에 새로운 일정을 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "일정 추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<GroupScheduleResponse> createSchedule(
            @PathVariable Long groupId,
            @RequestBody GroupScheduleRequest request,
            HttpServletRequest httpRequest
    ) {
        GroupSchedule schedule = groupScheduleService.createSchedule(groupId, request, httpRequest);
        GroupScheduleResponse response = new GroupScheduleResponse(schedule);
        return ResponseEntity.status(201).body(response);
    }
}