package com.chingubackend.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ScheduleRequest {
    private Long userId;
    private String title;
    private String description;
    private LocalDate scheduleDate;
}
