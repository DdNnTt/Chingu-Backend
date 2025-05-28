package com.chingubackend.controller;

import com.chingubackend.dto.request.GroupScheduleRequest;
import com.chingubackend.dto.response.GroupScheduleResponse;
import com.chingubackend.entity.GroupSchedule;
import com.chingubackend.service.GroupScheduleService;
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