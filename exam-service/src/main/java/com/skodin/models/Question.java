package com.skodin.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Schema(name = "question", example = "3 + 2 = ?")
    private String question;

    @Schema(name = "answer", example = "5")
    private String answer;
}
