package com.chingubackend.controller;

import com.chingubackend.dto.request.ScheduleRequest;
import com.chingubackend.entity.Schedule;
import com.chingubackend.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<Schedule> createSchedule(@RequestBody ScheduleRequest scheduleRequest) {
        Schedule savedSchedule = scheduleService.addSchedule(scheduleRequest);
        return ResponseEntity.ok(savedSchedule);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Schedule>> getSchedulesByUser(@PathVariable Long userId) {
        List<Schedule> schedules = scheduleService.getSchedulesByUserId(userId);
        return ResponseEntity.ok(schedules);
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<Schedule> updateSchedule(
            @PathVariable Long scheduleId,
            @RequestBody ScheduleRequest request) {

        Schedule updatedSchedule = scheduleService.updateSchedule(scheduleId, request);
        return ResponseEntity.ok(updatedSchedule);
    }

    @DeleteMapping("/{scheduelId}")
    public ResponseEntity<Map<String, String>> deleteScheule(@PathVariable Long scheduelId) {
        scheduleService.deleteSchedule(scheduelId);
        return ResponseEntity.ok(Map.of("message", "스케줄이 성공적으로 삭제 되었습니다."));
    }
}
