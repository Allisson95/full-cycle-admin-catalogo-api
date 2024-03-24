package com.github.allisson95.codeflix.domain.castmember;

import java.util.Objects;

import com.github.allisson95.codeflix.domain.Identifier;
import com.github.allisson95.codeflix.domain.utils.IdUtils;

public class CastMemberID extends Identifier {

    private final String value;

    private CastMemberID(final String anId) {
        Objects.requireNonNull(anId);
        this.value = anId;
    }

    public static CastMemberID from(final String anId) {
        return new CastMemberID(anId);
    }

    public static CastMemberID unique() {
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
        final CastMemberID other = (CastMemberID) o;
        return Objects.equals(getValue(), other.getValue());
    }

}
