package com.chingubackend.service;

import com.chingubackend.dto.request.ScheduleRequest;
import com.chingubackend.entity.Schedule;
import com.chingubackend.entity.User;
import com.chingubackend.repository.ScheduleRepository;
import com.chingubackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Schedule> getSchedulesByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디 입니다. 확인 부탁드리겠습니다. : " + userId));
        return scheduleRepository.findByUser(user);
    }

    public Schedule updateSchedule(Long scheduleId, ScheduleRequest request) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with id: " + scheduleId));

        // **필드별 null 체크 후 업데이트**
        if (request.getTitle() != null) {
            schedule.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            schedule.setDescription(request.getDescription());
        }
        if (request.getScheduleDate() != null) {
            schedule.setScheduleDate(request.getScheduleDate());
        }

        return scheduleRepository.save(schedule);
    }
}
