package com.chingubackend.service;

import com.chingubackend.dto.request.GroupScheduleRequest;
import com.chingubackend.dto.response.GroupScheduleResponse;
import com.chingubackend.entity.Group;
import com.chingubackend.entity.GroupSchedule;
import com.chingubackend.entity.User;
import com.chingubackend.exception.ForbiddenException;
import com.chingubackend.exception.NotFoundException;
import com.chingubackend.repository.GroupRepository;
import com.chingubackend.repository.GroupScheduleRepository;
import com.chingubackend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupScheduleService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupScheduleRepository groupScheduleRepository;

    @Transactional
    public GroupSchedule createSchedule(Long groupId, GroupScheduleRequest request, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다."));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("그룹을 찾을 수 없습니다."));

        GroupSchedule schedule = GroupSchedule.builder()
                .group(group)
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .scheduleDate(request.getScheduleDate().atStartOfDay())
                .build();

        return groupScheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteSchedule(Long groupId, Long scheduleId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("그룹을 찾을 수 없습니다."));

        GroupSchedule schedule = groupScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다."));

        if (!schedule.getGroup().getId().equals(group.getId())) {
            throw new IllegalArgumentException("해당 일정은 그룹에 속하지 않습니다.");
        }

        if (!schedule.getUser().getId().equals(userId)) {
            throw new ForbiddenException("삭제 권한이 없습니다.");
        }

        groupScheduleRepository.delete(schedule);
    }
}