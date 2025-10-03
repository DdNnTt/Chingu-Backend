package com.chingubackend.repository;

import com.chingubackend.entity.Group;
import com.chingubackend.entity.GroupInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupInviteRepository extends JpaRepository<GroupInvite, Long> {

    @Query("SELECT gi FROM GroupInvite gi JOIN FETCH gi.group WHERE gi.group.id = :groupId")
    List<GroupInvite> findAllByGroupIdWithGroup(@Param("groupId") Long groupId);

    @Query("SELECT gi FROM GroupInvite gi JOIN FETCH gi.group WHERE gi.receiver.id = :receiverId")
    List<GroupInvite> findByReceiverIdWithGroup(@Param("receiverId") Long receiverId);

    // 특정 사용자가 receiver 인 초대 삭제
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM GroupInvite gi WHERE gi.receiver.id = :receiverId")
    int deleteByReceiverId(@Param("receiverId") Long receiverId);

    // 특정 사용자가 sender 인 초대 삭제
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM GroupInvite gi WHERE gi.sender.id = :senderId")
    int deleteBySenderId(@Param("senderId") Long senderId);

    // 그룹과 연관된 초대 전체 삭제
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    void deleteByGroup(Group group);
}