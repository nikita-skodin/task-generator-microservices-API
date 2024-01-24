package com.skodin.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skodin.dtos.CreateExamRequest;
import com.skodin.exceptions.NotFoundException;
import com.skodin.models.Exam;
import com.skodin.models.Question;
import com.skodin.models.Section;
import com.skodin.services.ExamService;
import com.skodin.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ExamControllerTest {

    @Value("${url.root}")
    private String baseUrl;

    @InjectMocks
    ExamController examController;
    @Mock
    ExamService examService;
    @Mock
    UserService userService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private final String invalidHeader = "InvalidHeader";

    private final String headerWithTeacherRole = "Bearer token";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(examController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createExam_validHeaderAndValidCreateExamRequest_createsExam() throws Exception {
        CreateExamRequest request = new CreateExamRequest(
                "ExamName",
                Map.of("History", 2)
        );
        String requestJson = objectMapper.writeValueAsString(request);

        when(userService.isTeacher(anyString())).thenReturn(true);
        when(examService.createExam(request.getExamName(), request.getParams()))
                .thenReturn(new Exam("1", request.getExamName(), null));

        mockMvc.perform(post("/api/exam")
                        .header("Authorization", headerWithTeacherRole)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "%s/api/exam/%s".formatted(baseUrl, "1")));

        verify(examService, times(1)).createExam(
                request.getExamName(), request.getParams());
    }

    @Test
    void createExam_invalidHeader_forbidden() throws Exception {
        CreateExamRequest request = new CreateExamRequest(
                "ExamName",
                Map.of("History", 2)
        );
        String requestJson = objectMapper.writeValueAsString(request);

        when(userService.isTeacher(anyString())).thenReturn(false);

        mockMvc.perform(post("/api/exam")
                        .header("Authorization", invalidHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isForbidden());

        verify(examService, never()).createExam(
                request.getExamName(), request.getParams());
    }

    @Test
    void createExam_invalidCreateExamRequest_badRequest() throws Exception {
        CreateExamRequest request = new CreateExamRequest(
                "",
                Map.of("History", 2)
        );
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/exam")
                        .header("Authorization", invalidHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(examService, never()).createExam(
                request.getExamName(), request.getParams());
    }

    @Test
    void getExamById_invalidHeader_forbidden() throws Exception {

        when(userService.isTeacher(anyString())).thenReturn(false);
        when(userService.isStudent(anyString())).thenReturn(false);

        mockMvc.perform(get("/api/exam/1")
                        .header("Authorization", invalidHeader))
                .andExpect(status().isForbidden());

        verify(examService, never()).getById(anyString());
    }

    @Test
    void getExamById_tokenHasTeacherRole_returnsExam() throws Exception {
        Section section = new Section(
                List.of(new Question("question", "answer")));

        Exam exam = new Exam("name",
                List.of(section));

        when(userService.isTeacher(anyString())).thenReturn(true);
        when(examService.getById(anyString())).thenReturn(exam);

        mockMvc.perform(get("/api/exam/1")
                        .header("Authorization", headerWithTeacherRole)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sections[0].questions[0].answer")
                        .value("answer"));

        verify(examService, times(1)).getById("1");
    }

    @Test
    void getExamById_tokenHasStudentRole_returnsExamWithQuestionsWithoutAnswers() throws Exception {
        Section section = new Section(
                List.of(new Question("question", "answer")));

        Exam exam = new Exam("name",
                List.of(section));

        when(userService.isStudent(anyString())).thenReturn(true);
        when(examService.getById(anyString())).thenReturn(exam);

        mockMvc.perform(get("/api/exam/1")
                        .header("Authorization", headerWithTeacherRole)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sections[0].questions[0].answer")
                        .value("?"));

        verify(examService, times(1)).getById("1");
    }

    @Test
    void getExamById_noExamWithSuchId_NotFound() throws Exception {
        when(userService.isTeacher(anyString())).thenReturn(true);


        when(examService.getById(anyString())).thenThrow(new NotFoundException("message"));

        mockMvc.perform(get("/api/exam/1")
                        .header("Authorization", headerWithTeacherRole)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(examService, times(1)).getById("1");
    }
}