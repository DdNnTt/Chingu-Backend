package com.chingubackend.controller;

import com.chingubackend.dto.request.GroupScheduleRequest;
import com.chingubackend.dto.response.GroupScheduleResponse;
import com.chingubackend.entity.GroupSchedule;
import com.chingubackend.service.GroupScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @DeleteMapping("/{scheduleId}")
    @Operation(summary = "그룹 캘린더 일정 삭제", description = "로그인한 작성자만 자신의 그룹 캘린더 일정을 삭제할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일정 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "일정 또는 그룹 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> deleteSchedule(
            @PathVariable Long groupId,
            @PathVariable Long scheduleId,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        groupScheduleService.deleteSchedule(groupId, scheduleId, userId);

        return ResponseEntity.ok().body(Map.of(
                "message", "캘린더 일정이 성공적으로 삭제되었습니다.",
                "scheduleId", scheduleId,
                "groupId", groupId
        ));
    }

    @GetMapping("/{scheduleId}")
    @Operation(summary = "그룹 캘린더 일정 상세 조회", description = "특정 그룹 내 특정 일정의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일정 상세 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "일정 또는 그룹 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<GroupScheduleResponse> getScheduleDetail(
            @PathVariable Long groupId,
            @PathVariable Long scheduleId,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        GroupSchedule schedule = groupScheduleService.getScheduleDetail(groupId, scheduleId);

        GroupScheduleResponse response = new GroupScheduleResponse(schedule);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "그룹 캘린더 일정 리스트 조회", description = "특정 그룹에 속한 모든 캘린더 일정을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "그룹 스케줄 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "그룹 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<List<GroupScheduleResponse>> getSchedules(
            @PathVariable Long groupId,
            HttpServletRequest request
    ) {
        List<GroupSchedule> schedules = groupScheduleService.getSchedules(groupId);
        List<GroupScheduleResponse> responseList = schedules.stream()
                .map(GroupScheduleResponse::new)
                .toList();
        return ResponseEntity.ok(responseList);
    }
}