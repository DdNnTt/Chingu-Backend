package com.chingubackend.repository;

import com.chingubackend.entity.FriendshipScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendshipScoreRepository extends JpaRepository<FriendshipScore, Long> {
    Optional<FriendshipScore> findByUserIdAndFriendUserId(Long userId, Long friendUserId);
    List<FriendshipScore> findByUserIdOrFriendUserId(Long userId, Long friendUserId);
    List<FriendshipScore> findByFriendUserId(Long friendUserId);

}

