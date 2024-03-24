package com.github.allisson95.codeflix.domain.genre;

import java.util.Objects;

import com.github.allisson95.codeflix.domain.Identifier;
import com.github.allisson95.codeflix.domain.utils.IdUtils;

public class GenreID extends Identifier {

    private final String value;

    private GenreID(final String value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public static GenreID from(final String anId) {
        return new GenreID(anId);
    }

    public static GenreID unique() {
        return from(IdUtils.uuid());
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (getClass() != o.getClass())
            return false;
        final GenreID other = (GenreID) o;
        return Objects.equals(getValue(), other.getValue());
    }

}
