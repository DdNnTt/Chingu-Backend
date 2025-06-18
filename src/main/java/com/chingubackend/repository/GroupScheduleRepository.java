package com.chingubackend.repository;

import com.chingubackend.entity.Group;
import com.chingubackend.entity.GroupSchedule;
import com.chingubackend.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupScheduleRepository extends JpaRepository<GroupSchedule, Long> {
    List<GroupSchedule> findByGroup(Group group);
    void deleteByUser(User user);
}