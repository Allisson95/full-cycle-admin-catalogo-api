package com.github.allisson95.codeflix.domain.exceptions;

public class InternalErrorException extends NoStackTraceException {

    public InternalErrorException(final String message) {
        super(message);
    }

    public InternalErrorException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
