package com.chingubackend.repository;

import com.chingubackend.entity.QuizSetQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizSetQuestionRepository extends JpaRepository<QuizSetQuestion, Long> {

    List<QuizSetQuestion> findByQuizSetId(Long quizSetId);
}
