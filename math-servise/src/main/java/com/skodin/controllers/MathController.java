package com.skodin.controllers;

import com.skodin.models.Question;
import com.skodin.services.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/math")
public class MathController {

    private final QuestionService questionService;

    @GetMapping("/questions")
    public List<Question> getQuestions(@RequestParam Integer amount){
        amount = Math.abs(amount);
        return questionService.getQuestions(amount);
    }

}

