package com.skodin.controllers;

import com.skodin.models.Question;
import com.skodin.services.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class HistoryController {

    private final QuestionService questionService;

    @GetMapping("/questions")
    public List<Question> getQuestions(@RequestParam Integer amount){
        return questionService.getQuestions(amount);
    }

    @PostMapping("/questions")
    public Question addQuestion(@RequestBody Question question){
        return questionService.saveAndFlush(question);
    }

}

