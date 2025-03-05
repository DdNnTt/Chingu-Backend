package com.chingubackend.controller;

import com.chingubackend.dto.request.ScheduleRequest;
import com.chingubackend.entity.Schedule;
import com.chingubackend.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
