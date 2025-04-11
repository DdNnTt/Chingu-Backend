package com.chingubackend.controller;

import com.chingubackend.dto.request.ScheduleRequest;
import com.chingubackend.entity.Schedule;
import com.chingubackend.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Schedule> createSchedule(@RequestBody ScheduleRequest scheduleRequest) {
        Schedule savedSchedule = scheduleService.addSchedule(scheduleRequest);
        return ResponseEntity.ok(savedSchedule);
    }

    @Operation(summary = "스케줄 조회", description = "로그인 한 사용자가 등록한 개인 스케줄을 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<List<Schedule>> getSchedulesByUser(
            @Parameter(description = "로그인 한 사용자의 id", example = "1") @PathVariable Long userId) {
        List<Schedule> schedules = scheduleService.getSchedulesByUserId(userId);
        return ResponseEntity.ok(schedules);
    }

    @Operation(summary = "스케줄 수정", description = "로그인 한 사용자가 등록한 개인 스케줄을 수정합니다.")
    @PutMapping("/{scheduleId}")
    public ResponseEntity<Schedule> updateSchedule(
            @Parameter(description = "수정할 스케줄의 ID", example = "1") @PathVariable Long scheduleId,
            @RequestBody ScheduleRequest request) {

        Schedule updatedSchedule = scheduleService.updateSchedule(scheduleId, request);
        return ResponseEntity.ok(updatedSchedule);
    }
    @Operation(summary = "스케줄 삭제", description = "로그인 한 사용자가 등록한 개인 스케줄을 삭제합니다.")
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Map<String, String>> deleteSchedule(
            @Parameter(description = "삭제할 게시글의 id", example = "1") @PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.ok(Map.of("message", "스케줄이 성공적으로 삭제 되었습니다."));
    }
}
