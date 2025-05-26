package com.example.salarymanagementsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class AdminOperationException extends RuntimeException {
    public AdminOperationException(String message) {
        super(message);
    }
}
