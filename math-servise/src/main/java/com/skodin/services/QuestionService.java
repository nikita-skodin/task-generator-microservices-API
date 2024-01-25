package com.skodin.services;

import com.skodin.models.Question;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class QuestionService {

    private static final Random random = new Random();

    public List<Question> getQuestions(Integer count) {

        ArrayList<Question> list = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            list.add(getRandomQuestion());
        }

        return list;
    }

    private Question getRandomQuestion() {

        int a = random.nextInt(11);

        int b = random.nextInt(11);

        return new Question(
                a + " + " + b + " = ?",
                String.valueOf(a + b)
        );
    }

}
