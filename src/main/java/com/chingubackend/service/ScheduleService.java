package com.chingubackend.service;

import com.chingubackend.dto.request.ScheduleRequest;
import com.chingubackend.entity.Schedule;
import com.chingubackend.entity.User;
import com.chingubackend.exception.ForbiddenException;
import com.chingubackend.exception.NotFoundException;
import com.chingubackend.exception.SuccessResponse;
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

    public SuccessResponse addSchedule(Long userId, ScheduleRequest scheduleRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자가 존재하지 않습니다."));

        Schedule schedule = Schedule.builder()
                .user(user)
                .title(scheduleRequest.getTitle())
                .description(scheduleRequest.getDescription())
                .scheduleDate(scheduleRequest.getScheduleDate())
                .build();

        scheduleRepository.save(schedule);
        return SuccessResponse.of("일정이 성공적으로 등록되었습니다.");
    }

    public List<Schedule> getSchedulesByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자가 존재하지 않습니다."));
        return scheduleRepository.findByUser(user);
    }

    public SuccessResponse updateSchedule(Long userId, Long scheduleId, ScheduleRequest request) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("해당 스케줄을 찾을 수 없습니다."));

        if (!schedule.getUser().getId().equals(userId)) {
            throw new ForbiddenException("해당 스케줄에 대한 수정 권한이 없습니다.");
        }

        if (request.getTitle() != null) {
            schedule.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            schedule.setDescription(request.getDescription());
        }
        if (request.getScheduleDate() != null) {
            schedule.setScheduleDate(request.getScheduleDate());
        }

        scheduleRepository.save(schedule);
        return SuccessResponse.of("일정이 성공적으로 수정되었습니다.");
    }

    public SuccessResponse deleteSchedule(Long userId, Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("해당 스케줄을 찾을 수 없습니다."));

        if (!schedule.getUser().getId().equals(userId)) {
            throw new ForbiddenException("해당 스케줄에 대한 삭제 권한이 없습니다.");
        }

        scheduleRepository.delete(schedule);
        return SuccessResponse.of("일정이 성공적으로 삭제되었습니다.");
    }
}
