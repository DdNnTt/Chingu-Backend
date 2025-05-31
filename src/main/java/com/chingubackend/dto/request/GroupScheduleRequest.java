package com.chingubackend.dto.request;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupScheduleRequest {
    private String title;
    private String description;
    private LocalDate scheduleDate;
}
