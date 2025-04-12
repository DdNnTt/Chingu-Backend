package com.chingubackend.repository;

import com.chingubackend.entity.GroupInvite;
import com.chingubackend.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupInviteRepository extends JpaRepository<GroupInvite, Long> {
    Optional<GroupInvite> findByGroupIdAndSenderIdAndReceiverId(Long groupId, Long senderId, Long receiverId);
    List<GroupInvite> findByGroupIdAndReceiverId(Long groupId, Long receiverId);
    List<GroupInvite> findByGroupIdAndRequestStatus(Long groupId, RequestStatus status);
}