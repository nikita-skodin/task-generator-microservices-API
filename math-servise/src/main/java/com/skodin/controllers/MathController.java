package com.skodin.controllers;

import com.skodin.dtos.ErrorDTO;
import com.skodin.models.Question;
import com.skodin.services.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @Operation(
            summary = "Creates new math question",
            description = "Creates a new math question with numbers from 1 to 10",
            parameters = {
                    @Parameter(
                            name = "amount",
                            description = "amount of questions",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "String"))},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully Created", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Question.class)
                    )),
                    @ApiResponse(responseCode = "401", description = "Unauthorized, token is invalid", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )),
                    @ApiResponse(responseCode = "503", description = "Service unavailable", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    ))
            }
    )
    @GetMapping("/questions")
    public ResponseEntity<List<Question>> getQuestions(@RequestParam Integer amount) {
        amount = Math.abs(amount);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(questionService.getQuestions(amount));
    }

}

