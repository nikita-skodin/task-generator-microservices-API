package com.skodin.reactiveauthservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidToken extends ResponseStatusException {
    public InvalidToken(String reason) {
        super(HttpStatus.FORBIDDEN, reason);
    }
}
