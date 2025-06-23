package com.chingubackend.repository;

import com.chingubackend.entity.GroupMember;
import com.chingubackend.model.MemberStatus;
import com.chingubackend.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    List<GroupMember> findByGroupId(Long groupId);
    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);
    List<GroupMember> findByUserIdAndStatus(Long userId, MemberStatus status);


    void deleteByUserId(Long userId);
    boolean existsByGroupIdAndUserId(Long groupId, Long userId);
}