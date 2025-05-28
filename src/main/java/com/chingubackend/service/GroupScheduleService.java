package com.chingubackend.service;

import com.chingubackend.dto.request.GroupScheduleRequest;
import com.chingubackend.dto.response.GroupScheduleResponse;
import com.chingubackend.entity.Group;
import com.chingubackend.entity.GroupSchedule;
import com.chingubackend.entity.User;
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
        // 필터에서 저장한 userId 꺼내기
        Long userId = (Long) httpRequest.getAttribute("userId");

        // 사용자 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다."));

        // 그룹 조회
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("그룹을 찾을 수 없습니다."));

        // 일정 생성
        GroupSchedule schedule = GroupSchedule.builder()
                .group(group)
                .user(user) // 작성자 자동 설정
                .title(request.getTitle())
                .description(request.getDescription())
                .scheduleDate(request.getScheduleDate().atStartOfDay())
                .build();

        return groupScheduleRepository.save(schedule);
    }
}