package com.chingubackend.repository;


import com.chingubackend.entity.QuizSet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizSetRepository extends JpaRepository<QuizSet, Long> {
    Optional<QuizSet> findFirstByCreatorUserIdOrderByCreatedAtDesc(Long creatorUserId);
    List<QuizSet> findByCreatorUserIdOrderByCreatedAtDesc(Long creatorUserId);
}