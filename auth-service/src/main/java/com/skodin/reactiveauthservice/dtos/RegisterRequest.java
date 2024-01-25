package com.skodin.reactiveauthservice.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RegisterRequest {
    @Email(message = "email is invalid")
    @NotBlank(message = "email cannot be blank")
    private String email;

    @NotBlank(message = "username cannot be blank")
    private String username;

    @NotBlank(message = "password cannot be blank")
    private String password;

    @Pattern(regexp = "ROLE_TEACHER|ROLE_STUDENT", message = "Invalid enum value, try ROLE_TEACHER or ROLE_STUDENT")
    private String role;
}
