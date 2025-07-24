package com.chingubackend.repository;

import com.chingubackend.entity.Friend;
import com.chingubackend.entity.User;
import com.chingubackend.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    boolean existsByUserIdAndFriendId(Long userId, Long friendId);
    boolean existsByUserIdAndFriendIdAndRequestStatus(Long userId, Long friendId, RequestStatus status);

    Optional<Friend> findByUserIdAndFriendIdAndRequestStatus(Long friendId, Long userId, RequestStatus requestStatus);

    @Query("SELECT f FROM Friend f WHERE f.friendId = :userId AND f.requestStatus = 'PENDING'")
    List<Friend> findPendingRequestsForUser(@Param("userId") Long userId);

    @Query("SELECT f FROM Friend f WHERE (f.userId = :userId OR f.friendId = :userId) AND f.requestStatus = 'ACCEPTED'")
    List<Friend> findAllAcceptedFriends(@Param("userId") Long userId);

    @Query("SELECT f FROM Friend f WHERE ((f.userId = :userId AND f.friendId = :friendId) OR (f.userId = :friendId AND f.friendId = :userId)) AND f.requestStatus = 'ACCEPTED'")
    Optional<Friend> findAcceptedFriend(@Param("userId") Long userId, @Param("friendId") Long friendId);
    
    @Query("SELECT COUNT(f) > 0 FROM Friend f WHERE f.userId = :userId AND f.friendId = :friendId")
    boolean existsFriendship(@Param("userId") Long userId, @Param("friendId") Long friendId);

    List<Friend> findByUserIdOrFriendId(Long userId, Long friendId);
}
