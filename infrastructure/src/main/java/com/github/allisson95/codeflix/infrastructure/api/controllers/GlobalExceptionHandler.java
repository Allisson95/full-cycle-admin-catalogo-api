package com.github.allisson95.codeflix.infrastructure.api.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.github.allisson95.codeflix.domain.exceptions.DomainException;
import com.github.allisson95.codeflix.domain.validation.Error;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ DomainException.class })
    public ResponseEntity<Object> handleDomainException(final DomainException ex) {
        return ResponseEntity.unprocessableEntity().body(ApiError.from(ex));
    }

    record ApiError(List<Error> errors) {

        public static ApiError from(final DomainException ex) {
            return new ApiError(ex.getErrors());
        }

    }

}
