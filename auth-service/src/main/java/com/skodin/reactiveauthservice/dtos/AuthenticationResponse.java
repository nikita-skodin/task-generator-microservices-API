package com.skodin.reactiveauthservice.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String accessToken;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String refreshToken;
}
