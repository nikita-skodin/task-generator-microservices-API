package com.skodin.services;

import com.skodin.models.Exam;
import com.skodin.models.Question;
import com.skodin.models.Section;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service

@RequiredArgsConstructor
public class ExamService {

    private final RestTemplate restTemplate;

    @Value("${eureka.instance.instance-id}")
    private String instantName;

    public Exam createExam(Map<String, Integer> map) {

        List<Section> list = map.entrySet()
                .stream()
                .map(this::getURL)
                .map(url -> restTemplate.getForObject(url, Question[].class))
                .map(Arrays::asList)
                .map(Section::new)
                .toList();

        return new Exam(instantName, list);
    }

    private String getURL(Map.Entry<String, Integer> map) {
        String string = "http://" + map.getKey() + "/api/questions?amount=" + map.getValue();
        System.err.println(string);
        return string;
    }

}
