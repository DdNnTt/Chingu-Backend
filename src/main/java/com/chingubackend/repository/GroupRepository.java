package com.chingubackend.repository;

import com.chingubackend.entity.Group;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByCreatorId(Long creatorId);
    @Modifying
    @Query("UPDATE Group g SET g.creator.id = :newCreatorId WHERE g.creator.id = :originalCreatorId")
    void updateCreatorId(@Param("originalCreatorId") Long originalCreatorId, @Param("newCreatorId") Long newCreatorId);
}
