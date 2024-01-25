package com.skodin.reactiveauthservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AccountDisableException extends ResponseStatusException {
    public AccountDisableException(String reason) {
        super(HttpStatus.UNAUTHORIZED, reason);
    }
}