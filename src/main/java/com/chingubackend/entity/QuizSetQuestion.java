package com.chingubackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quiz_set_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizSetQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quiz_set_id", nullable = false)
    private Long quizSetId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "user_selected_answer", nullable = false)
    private Integer userSelectedAnswer;
}
