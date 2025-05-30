package com.chingubackend.dto.response;

import com.chingubackend.entity.GroupSchedule;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class GroupScheduleResponse {
    private Long scheduleId;
    private Long groupId;
    private String nickname;
    private String title;
    private String description;
    private LocalDateTime scheduleDate;
    private LocalDateTime createdAt;

    public GroupScheduleResponse(GroupSchedule schedule) {
        this.scheduleId = schedule.getId();
        this.groupId = schedule.getGroup().getId();
        this.nickname = schedule.getUser().getNickname();
        this.title = schedule.getTitle();
        this.description = schedule.getDescription();
        this.scheduleDate = schedule.getScheduleDate();
        this.createdAt = schedule.getCreatedAt();
    }
}