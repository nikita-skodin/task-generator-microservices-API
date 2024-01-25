package com.skodin.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skodin.models.Question;
import com.skodin.services.QuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class MathControllerTest {

    @Mock
    private QuestionService questionService;

    @InjectMocks
    private MathController mathController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mathController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getQuestion_returnsListOfQuestions() throws Exception {

        int amount = 5;
        List<Question> expectedQuestions = List.of(
                new Question("1 + 1 = ?", "2"),
                new Question("2 + 2 = ?", "4"),
                new Question("3 + 3 = ?", "6"),
                new Question("4 + 4 = ?", "8"),
                new Question("5 + 5 = ?", "10")
        );

        String expectedJson = objectMapper.writeValueAsString(expectedQuestions);

        Mockito.when(questionService.getQuestions(5)).thenReturn(expectedQuestions);

        mathController.getQuestions(amount);

        mockMvc.perform(get("/api/math/questions?amount=5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(expectedJson));
    }
}