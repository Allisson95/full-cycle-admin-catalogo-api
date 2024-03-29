package com.github.allisson95.codeflix.domain.category;

import java.util.Objects;

import com.github.allisson95.codeflix.domain.Identifier;
import com.github.allisson95.codeflix.domain.utils.IdUtils;

public class CategoryID extends Identifier {

    private final String value;

    private CategoryID(final String value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public static CategoryID from(final String anId) {
        return new CategoryID(anId);
    }

    public static CategoryID unique() {
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
        final CategoryID other = (CategoryID) o;
        return Objects.equals(getValue(), other.getValue());
    }

}
