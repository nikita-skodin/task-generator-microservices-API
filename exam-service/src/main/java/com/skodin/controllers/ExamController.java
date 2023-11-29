package com.skodin.controllers;

import com.skodin.models.Exam;
import com.skodin.services.ExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/api/exam")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @GetMapping
    public Exam createExam(@RequestBody Map<String, Integer> map) {
        log.log(Level.DEBUG, map);

        return examService.createExam(map);
    }

}
