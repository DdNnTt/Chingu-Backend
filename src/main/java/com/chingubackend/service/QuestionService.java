package com.chingubackend.service;

import com.chingubackend.dto.response.QuestionResponse;
import com.chingubackend.entity.Question;
import com.chingubackend.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public List<QuestionResponse> getAllQuestions() {
        List<Question> questions = questionRepository.findAll();

        return questions.stream()
                .map(q -> new QuestionResponse(
                        q.getId(),
                        q.getContent(),
                        q.getOption1(),
                        q.getOption2(),
                        q.getOption3(),
                        q.getOption4()
                ))
                .toList();
    }

    public List<QuestionResponse> getRandomTenQuestions() {
        List<Question> questions = questionRepository.findRandomQuestions(10);

        return questions.stream()
                .map(q -> new QuestionResponse(
                        q.getId(),
                        q.getContent(),
                        q.getOption1(),
                        q.getOption2(),
                        q.getOption3(),
                        q.getOption4()
                ))
                .toList();
    }


}
