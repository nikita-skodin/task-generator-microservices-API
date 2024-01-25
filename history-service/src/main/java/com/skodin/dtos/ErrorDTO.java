package com.skodin.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorDTO {
    @Schema(name = "error", example = "Bad Request")
    String error;

    @Schema(name = "message", example = "some_field - must not be blank")
    String message;
}