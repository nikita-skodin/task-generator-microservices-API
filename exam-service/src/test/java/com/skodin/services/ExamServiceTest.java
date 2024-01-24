package com.skodin.services;

import com.skodin.exceptions.NotFoundException;
import com.skodin.models.Exam;
import com.skodin.models.Question;
import com.skodin.repository.ExamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamServiceTest {

    @InjectMocks
    private ExamService examService;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ExamRepository examRepository;

    @Test
    void createExam_SuccessfullySaveExam() {
        String examName = "TestExamName";
        Map<String, Integer> params = Map.of("service1", 5, "service2", 10);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);

        when(restTemplate.getForObject(urlCaptor.capture(), eq(Question[].class)))
                .thenReturn(new Question[]{new Question("Question1", "Answer1")});

        when(examRepository.save(any(Exam.class))).thenReturn(new Exam(examName, null));

        examService.createExam(examName, params);

        verify(restTemplate, times(2)).getForObject(anyString(), eq(Question[].class));

        assertThat(urlCaptor.getAllValues())
                .containsExactlyInAnyOrder(
                        "http://service1/api/service1/questions?amount=5",
                        "http://service2/api/service2/questions?amount=10"
                );

        verify(examRepository, times(1)).save(any(Exam.class));
    }

    @Test
    void createExam_invalidServiceUrl_throwsNotFoundException() {
        String examName = "TestExamName";
        Map<String, Integer> params = Map.of("invalidService", 5);

        when(restTemplate.getForObject(anyString(), eq(Question[].class)))
                .thenThrow(IllegalStateException.class);

        assertThrows(NotFoundException.class, () -> examService.createExam(examName, params));

        verify(restTemplate, times(1)).getForObject(anyString(), eq(Question[].class));
        verify(examRepository, never()).save(any(Exam.class));
    }

    @Test
    void getById_existingId_returnsExam() {
        String existingId = "existingId";
        Exam expectedExam = new Exam(existingId, null);

        when(examRepository.findById(existingId)).thenReturn(Optional.of(expectedExam));

        Exam resultExam = examService.getById(existingId);

        assertEquals(expectedExam, resultExam);
        verify(examRepository, times(1)).findById(existingId);
    }

    @Test
    void getById_nonExistingId_throwsNotFoundException() {
        String nonExistingId = "nonExistingId";

        when(examRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> examService.getById(nonExistingId));
        verify(examRepository, times(1)).findById(nonExistingId);
    }

}