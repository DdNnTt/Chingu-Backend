package com.chingubackend.repository;


import com.chingubackend.entity.QuizSet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizSetRepository extends JpaRepository<QuizSet, Long> {
}