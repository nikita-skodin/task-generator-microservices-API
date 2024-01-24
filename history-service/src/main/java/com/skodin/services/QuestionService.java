package com.skodin.services;

import com.skodin.models.Question;
import com.skodin.repositories.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public List<Question> getQuestions(Integer count) {

        ArrayList<Question> list = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            list.add(getRandomQuestion());
        }

        return list;
    }

    private Question getRandomQuestion() {
        return questionRepository.findRandom();
    }

    public <S extends Question> S saveAndFlush(S entity) {
        return questionRepository.saveAndFlush(entity);
    }
}
