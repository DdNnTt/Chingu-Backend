package com.chingubackend.repository;

import com.chingubackend.entity.Group;
import com.chingubackend.entity.GroupInvite;
import com.chingubackend.model.RequestStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

public interface GroupInviteRepository extends JpaRepository<GroupInvite, Long> {
    @Query("SELECT gi FROM GroupInvite gi JOIN FETCH gi.group WHERE gi.group.id = :groupId")
    List<GroupInvite> findAllByGroupIdWithGroup(@Param("groupId") Long groupId);

    @Query("SELECT gi FROM GroupInvite gi JOIN FETCH gi.group WHERE gi.receiver.id = :receiverId")
    List<GroupInvite> findByReceiverIdWithGroup(@Param("receiverId") Long receiverId);

    void deleteBySenderIdOrReceiverId(Long senderId, Long receiverId);

    void deleteByGroup(Group group);
}