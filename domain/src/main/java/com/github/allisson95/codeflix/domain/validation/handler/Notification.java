package com.github.allisson95.codeflix.domain.validation.handler;

import java.util.ArrayList;
import java.util.List;

import com.github.allisson95.codeflix.domain.exceptions.DomainException;
import com.github.allisson95.codeflix.domain.validation.Error;
import com.github.allisson95.codeflix.domain.validation.ValidationHandler;

public class Notification implements ValidationHandler {

    private final List<Error> errors;

    private Notification(final List<Error> errors) {
        this.errors = errors;
    }

    public static Notification create() {
        return new Notification(new ArrayList<>());
    }

    public static Notification create(final Throwable t) {
        return create(new Error(t.getMessage()));
    }

    public static Notification create(final Error anError) {
        return new Notification(new ArrayList<>()).append(anError);
    }

    @Override
    public Notification append(Error anError) {
        this.errors.add(anError);
        return this;
    }

    @Override
    public Notification append(final ValidationHandler aHandler) {
        this.errors.addAll(aHandler.getErrors());
        return this;
    }

    @Override
    public Notification validate(Validation aValidation) {
        try {
            aValidation.validate();
        } catch (final DomainException ex) {
            this.errors.addAll(ex.getErrors());
        } catch (final Throwable t) {
            this.errors.add(new Error(t.getMessage()));
        }

        return this;
    }

    @Override
    public List<Error> getErrors() {
        return this.errors;
    }

}
