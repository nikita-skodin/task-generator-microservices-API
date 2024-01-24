package com.skodin.controllers;

import com.skodin.dtos.ErrorDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Set;

public class ParentController {
    @ExceptionHandler
    private ResponseEntity<ErrorDTO> handleException(ConstraintViolationException e) {

        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();

        StringBuilder response = new StringBuilder();

        for (var el : constraintViolations) {
            response.append(el.getMessage()).append(";");
        }

        return ResponseEntity
                .status(400)
                .body(new ErrorDTO("BAD_REQUEST", response.toString().trim()));
    }

    @ExceptionHandler()
    public ResponseEntity<ErrorDTO> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = "Validation errors";

        ErrorDTO body = new ErrorDTO();
        body.setError(errorMessage);

        ex.getBindingResult().getAllErrors().forEach(error -> {
            if (error instanceof FieldError fieldError) {
                body.setMessage(String.format("%s - %s", fieldError.getField(), fieldError.getDefaultMessage()));
            } else {
                body.setMessage(String.format("%s", error.getDefaultMessage()));
            }
        });
        return ResponseEntity.badRequest().body(body);
    }
}
