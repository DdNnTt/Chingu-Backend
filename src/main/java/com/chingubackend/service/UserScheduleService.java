package com.chingubackend.service;

import com.chingubackend.dto.request.ScheduleRequest;
import com.chingubackend.entity.UserSchedule;
import com.chingubackend.repository.UserScheduleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UserScheduleService {
    private final UserScheduleRepository userScheduleRepository;

    public UserScheduleService(UserScheduleRepository userScheduleRepository) {
        this.userScheduleRepository = userScheduleRepository;
    }
    @Transactional
    public UserSchedule addSchedule(ScheduleRequest scheduleRequest) {

        UserSchedule userSchedule = UserSchedule.builder()
                .userId(scheduleRequest.getUserId())
                .title(scheduleRequest.getTitle())
                .description(scheduleRequest.getDescription())
                .scheduleDate(scheduleRequest.getScheduleDate())
                .build();

        return userScheduleRepository.save(userSchedule);
    }

}
