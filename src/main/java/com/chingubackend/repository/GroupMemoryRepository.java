package com.chingubackend.repository;

import com.chingubackend.entity.Group;
import com.chingubackend.entity.GroupMemory;
import com.chingubackend.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface GroupMemoryRepository extends JpaRepository<GroupMemory, Long> {
    @Modifying
    @Query("UPDATE GroupMemory gm SET gm.user = :systemUser WHERE gm.user = :user")
    void reassignMemoriesToSystem(@Param("user") User user, @Param("systemUser") User systemUser);

    List<GroupMemory> findByGroupId(Long groupId);
    List<GroupMemory> findByGroupIdOrderByCreatedDateDesc(Long groupId);

}
