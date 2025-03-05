package com.chingubackend.service;

import com.chingubackend.dto.request.ScheduleRequest;
import com.chingubackend.entity.Schedule;
import com.chingubackend.repository.ScheduleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public Schedule addSchedule(ScheduleRequest scheduleRequest) {
        Schedule schedule = Schedule.builder()
                .userId(scheduleRequest.getUserId())
                .title(scheduleRequest.getTitle())
                .description(scheduleRequest.getDescription())
                .scheduleDate(scheduleRequest.getScheduleDate())
                .build();

        return scheduleRepository.save(schedule);
    }
}
