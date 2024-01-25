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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/history")
public class HistoryController {

    private final QuestionService questionService;

    @Operation(
            summary = "Get history questions",
            description = "Get history questions",
            parameters = {
                    @Parameter(
                            name = "amount",
                            description = "amount of questions",
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "String"))},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully", content = @Content(
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
    public ResponseEntity<List<Question>> getQuestions(@RequestParam Integer amount){
        amount = Math.abs(amount);
        return ResponseEntity.ok(questionService.getQuestions(amount));
    }

    @Operation(
            summary = "Creates new history question",
            description = "Creates a new history question",
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
    @PostMapping("/questions")
    public ResponseEntity<Question> addQuestion(@RequestBody Question question){
        return ResponseEntity.ok(questionService.saveAndFlush(question));
    }

}

