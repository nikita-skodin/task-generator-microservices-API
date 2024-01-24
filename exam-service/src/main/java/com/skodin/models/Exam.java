package com.skodin.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("exams")
@NoArgsConstructor
@AllArgsConstructor
public class Exam {
    @Id
    @Schema(name = "id", example = "65af8ee49ae7937540a7bef0")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

    @Indexed
    @Schema(name = "name", example = "exam")
    private String name;

    @Schema(name = "sections")
    @JsonInclude(JsonInclude.Include.NON_NULL)  // ??
    private List<Section> sections;

    public Exam(String name, List<Section> sections) {
        this.name = name;
        this.sections = sections;
    }
}
