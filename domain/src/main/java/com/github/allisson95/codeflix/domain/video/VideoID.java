package com.github.allisson95.codeflix.domain.video;

import java.util.Objects;
import java.util.UUID;

import com.github.allisson95.codeflix.domain.Identifier;

public class VideoID extends Identifier {

    private final String value;

    private VideoID(final String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static VideoID from(final String anId) {
        return new VideoID(anId.toLowerCase());
    }

    public static VideoID from(final UUID anId) {
        return from(anId.toString());
    }

    public static VideoID unique() {
        return from(UUID.randomUUID());
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
        final VideoID other = (VideoID) o;
        return Objects.equals(getValue(), other.getValue());
    }

}
