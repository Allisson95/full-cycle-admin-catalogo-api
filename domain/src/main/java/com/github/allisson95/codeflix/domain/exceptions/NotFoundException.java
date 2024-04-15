package com.github.allisson95.codeflix.domain.exceptions;

import java.util.Collections;
import java.util.List;

import com.github.allisson95.codeflix.domain.AggregateRoot;
import com.github.allisson95.codeflix.domain.Identifier;
import com.github.allisson95.codeflix.domain.validation.Error;

public class NotFoundException extends DomainException {

    protected NotFoundException(final String aMessage, final List<Error> anErrors) {
        super(aMessage, anErrors);
    }

    public static NotFoundException with(
            final Class<? extends AggregateRoot<?>> anAggregate,
            final Identifier anId) {
        final var anError = "%s with id %s was not found".formatted(anAggregate.getSimpleName(), anId.getValue());
        return new NotFoundException(anError, Collections.emptyList());
    }

    public static NotFoundException with(final Error anError) {
        return new NotFoundException(anError.message(), List.of(anError));
    }

}
