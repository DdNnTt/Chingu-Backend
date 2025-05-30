package com.chingubackend.repository;

import com.chingubackend.entity.Group;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByCreatorId(Long creatorId);

}
