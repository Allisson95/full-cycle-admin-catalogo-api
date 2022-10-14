package com.github.allisson95.codeflix.domain.exceptions;

import java.util.List;

import com.github.allisson95.codeflix.domain.validation.Error;

public class DomainException extends NoStackTraceException {

    private final List<Error> errors;

    private DomainException(final List<Error> anErrors) {
        super("", null);
        this.errors = anErrors;
    }

    public static DomainException with(final Error anError) {
        return new DomainException(List.of(anError));
    }

    public static DomainException with(final List<Error> anErrors) {
        return new DomainException(anErrors);
    }

    public List<Error> getErrors() {
        return errors;
    }

}
