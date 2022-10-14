package com.github.allisson95.codeflix.domain;

import java.util.Objects;

import com.github.allisson95.codeflix.domain.validation.ValidationHandler;

public abstract class Entity<ID extends Identifier> {

    protected final ID id;

    protected Entity(final ID id) {
        Objects.requireNonNull(id, "'id' should not be null");
        this.id = id;
    }

    public abstract void validate(ValidationHandler aHandler);

    public ID getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (getClass() != o.getClass())
            return false;
        final Entity<?> other = (Entity<?>) o;
        return Objects.equals(getId(), other.getId());
    }

}
