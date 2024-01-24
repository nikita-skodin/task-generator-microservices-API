package com.skodin.controllers;

import com.skodin.dtos.CreateExamRequest;
import com.skodin.dtos.ErrorDTO;
import com.skodin.exceptions.AccessDeniedException;
import com.skodin.models.Exam;
import com.skodin.services.ExamService;
import com.skodin.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "ExamController", description = "The Exam API")
@Log4j2
@Validated
@RestController
@RequestMapping("/api/exam")
@RequiredArgsConstructor
public class ExamController extends ParentController {

    private final ExamService examService;
    private final UserService userService;

    @Value("${url.root}")
    private String baseUrl;

    @Operation(
            summary = "Create new Exam",
            description = "Create new Exam",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Create exam request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateExamRequest.class))),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully Created", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Exam.class)
                    )),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )),
                    @ApiResponse(responseCode = "401", description = "Unauthorized, token is invalid", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )),
                    @ApiResponse(responseCode = "403", description = "Forbidden, there are no suitable roles", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )),
                    @ApiResponse(responseCode = "503", description = "Service unavailable", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    ))
            }
    )
    @PostMapping
    public ResponseEntity<Exam> createExam(@Valid @RequestBody CreateExamRequest request,
                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String header) {

        if (!userService.isTeacher(header)) {
            throw new AccessDeniedException("Teacher roles has not been found");
        }

        log.info("Request map is: {}", request.getParams());
        Exam exam = examService.createExam(request.getExamName(), request.getParams());

        return ResponseEntity
                .created(URI.create("%s/api/exam/%s".formatted(baseUrl, exam.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(exam);
    }

    @Operation(
            summary = "Get Exam by id",
            description = "Get Exam by id",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "exam`s id",
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "String"))},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Exam.class)
                    )),
                    @ApiResponse(responseCode = "401", description = "Unauthorized, invalid token", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )),
                    @ApiResponse(responseCode = "404", description = "Exam with such id is Not Found", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )),
                    @ApiResponse(responseCode = "503", description = "Service unavailable", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    ))
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Exam> getExamById(@PathVariable("id") String id,
                                            @RequestHeader(HttpHeaders.AUTHORIZATION) String header) {

        if (!userService.isStudent(header) && !userService.isTeacher(header)){
                throw new AccessDeniedException("No roles has been found");
        }

        Exam exam = examService.getById(id);

        if (userService.isStudent(header)) {
            exam.getSections().forEach(section -> section.getQuestions().forEach(question -> question.setAnswer("?")));
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(exam);
    }
}

