package com.skodin.reactiveauthservice.controllers;

import com.skodin.reactiveauthservice.dtos.ErrorDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Log4j2
@ControllerAdvice
public class ValidationHandler {
    @ExceptionHandler()
    public ResponseEntity<ErrorDTO> handleException(Exception e) {
        log.warn("UnexpectedException: {}", e.getMessage(), e);
        return ResponseEntity.internalServerError()
                .body(new ErrorDTO(e.getClass().getSimpleName(), e.getMessage()));
    }
}