package com.chingubackend.repository;

import com.chingubackend.entity.Friend;
import com.chingubackend.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    boolean existsByUserIdAndFriendId(Long userId, Long friendId);
    boolean existsByUserIdAndFriendIdAndRequestStatus(Long userId, Long friendId, RequestStatus status);
}
