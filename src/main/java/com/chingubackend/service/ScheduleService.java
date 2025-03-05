package com.chingubackend.service;

import com.chingubackend.dto.request.ScheduleRequest;
import com.chingubackend.entity.Schedule;
import com.chingubackend.entity.User;
import com.chingubackend.repository.ScheduleRepository;
import com.chingubackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public Schedule addSchedule(ScheduleRequest scheduleRequest) {
        User user = userRepository.findById(scheduleRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디 입니다. 확인 부탁드리겠습니다. : " + scheduleRequest.getUserId()));

        Schedule schedule = Schedule.builder()
                .user(user)
                .title(scheduleRequest.getTitle())
                .description(scheduleRequest.getDescription())
                .scheduleDate(scheduleRequest.getScheduleDate())
                .build();

        return scheduleRepository.save(schedule);
    }
}
