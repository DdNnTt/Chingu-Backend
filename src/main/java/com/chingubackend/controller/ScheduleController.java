package com.chingubackend.controller;

import com.chingubackend.dto.request.ScheduleRequest;
import com.chingubackend.entity.Schedule;
import com.chingubackend.exception.SuccessResponse;
import com.chingubackend.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Tag(name = "SCHEDULE API", description = "일정 관련 API")
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @Operation(summary = "스케줄 등록", description = "로그인 한 사용자의 개인 스케줄을 등록합니다.")
    @PostMapping
    public ResponseEntity<SuccessResponse> createSchedule(
            @RequestBody ScheduleRequest scheduleRequest,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        SuccessResponse response = scheduleService.addSchedule(userId, scheduleRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "스케줄 조회", description = "로그인 한 사용자가 등록한 개인 스케줄을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<Schedule>> getSchedulesByUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Schedule> schedules = scheduleService.getSchedulesByUserId(userId);
        return ResponseEntity.ok(schedules);
    }

    @Operation(summary = "스케줄 수정", description = "로그인 한 사용자가 등록한 개인 스케줄을 수정합니다.")
    @PutMapping("/{scheduleId}")
    public ResponseEntity<SuccessResponse> updateSchedule(
            @Parameter(description = "수정할 스케줄의 ID", example = "1") @PathVariable Long scheduleId,
            @RequestBody ScheduleRequest requestBody,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        SuccessResponse response = scheduleService.updateSchedule(userId, scheduleId, requestBody);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "스케줄 삭제", description = "로그인 한 사용자가 등록한 개인 스케줄을 삭제합니다.")
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Map<String, String>> deleteSchedule(
            @Parameter(description = "삭제할 스케줄의 ID", example = "1") @PathVariable Long scheduleId,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        scheduleService.deleteSchedule(userId, scheduleId);
        return ResponseEntity.ok(Map.of("message", "스케줄이 성공적으로 삭제 되었습니다."));
    }
}
