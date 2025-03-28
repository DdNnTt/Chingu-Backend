package com.chingubackend.repository;

import com.chingubackend.entity.FriendshipScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendshipScoreRepository extends JpaRepository<FriendshipScore, Long> {
    Optional<FriendshipScore> findByUserIdAndFriendUserId(Long userId, Long friendUserId);
}

