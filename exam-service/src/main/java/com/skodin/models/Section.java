package com.skodin.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Section {

    @Schema(name = "questions")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Question> questions;
}
