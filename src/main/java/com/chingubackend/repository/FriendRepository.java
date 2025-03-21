package com.chingubackend.repository;

import com.chingubackend.entity.Friend;
import com.chingubackend.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    boolean existsByUserIdAndFriendId(Long userId, Long friendId);
    boolean existsByUserIdAndFriendIdAndRequestStatus(Long userId, Long friendId, RequestStatus status);

    Optional<Friend> findByUserIdAndFriendIdAndRequestStatus(Long friendId, Long userId, RequestStatus requestStatus);
}
