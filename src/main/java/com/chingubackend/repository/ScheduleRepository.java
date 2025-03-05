package com.chingubackend.repository;

import com.chingubackend.entity.Schedule;
import com.chingubackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByUser(User user);
}
