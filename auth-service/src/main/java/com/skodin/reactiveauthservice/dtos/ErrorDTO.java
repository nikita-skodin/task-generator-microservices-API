package com.skodin.reactiveauthservice.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String error;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String message;
}
