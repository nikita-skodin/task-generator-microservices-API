package com.skodin.services;

import com.skodin.exceptions.NotFoundException;
import com.skodin.models.Exam;
import com.skodin.models.Question;
import com.skodin.models.Section;
import com.skodin.repository.ExamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
@RequiredArgsConstructor
public class ExamService {

    private final RestTemplate restTemplate;
    private final ExamRepository examRepository;

    public Exam createExam(String examName, Map<String, Integer> params) {

        List<Section> list = params.entrySet()
                .stream()
                .map(this::getURL)
                .map(this::createRequest)
                .map(Arrays::asList)
                .map(Section::new)
                .toList();

        Exam exam = new Exam(examName, list);
        log.info("Exam successfully created");

        return examRepository.save(exam);
    }

    private Question[] createRequest(String url) {
        try {
            return restTemplate.getForObject(url, Question[].class);
        } catch (IllegalStateException e) {
            throw new NotFoundException("No such services for url: %s".formatted(url));
        }
    }

    private String getURL(Map.Entry<String, Integer> map) {
        String serverName = map.getKey().toLowerCase();

        String url = "http://%s/api/%s/questions?amount=%d".formatted(serverName, serverName, map.getValue());
        log.info("url: {}", url);
        return url;
    }

}
