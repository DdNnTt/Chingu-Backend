package com.chingubackend.repository;

import com.chingubackend.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query(value = "SELECT * FROM questions ORDER BY RAND() LIMIT :size", nativeQuery = true)
    List<Question> findRandomQuestions(@Param("size") int size);
}
