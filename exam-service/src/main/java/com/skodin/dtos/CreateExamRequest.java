package com.skodin.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateExamRequest {
    @NotBlank
    @Schema(name = "exam_name", example = "First exam")
    @JsonProperty(value = "exam_name")
    private String examName;

    @NotNull
    @Schema(name = "params", example = "{ \"MATH\": \"1\", \"HISTORY\": \"1\" }")
    private Map<String, Integer> params;
}
