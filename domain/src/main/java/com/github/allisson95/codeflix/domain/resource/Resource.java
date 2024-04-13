package com.github.allisson95.codeflix.domain.resource;

import java.util.Arrays;
import java.util.Objects;

import com.github.allisson95.codeflix.domain.ValueObject;

public class Resource extends ValueObject {

    private final String checksum;
    private final byte[] content;
    private final String contentType;
    private final String name;

    private Resource(final String checksum, final byte[] content, final String contentType, final String name) {
        this.checksum = Objects.requireNonNull(checksum);
        this.content = Objects.requireNonNull(content);
        this.contentType = Objects.requireNonNull(contentType);
        this.name = Objects.requireNonNull(name);
    }

    public static Resource of(final String checksum, final byte[] content, final String contentType, final String name) {
        return new Resource(checksum, content, contentType, name);
    }

    public String checksum() {
        return checksum;
    }

    public byte[] content() {
        return content;
    }

    public String contentType() {
        return contentType;
    }

    public String name() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(content);
        result = prime * result + Objects.hash(checksum, contentType, name);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Resource other = (Resource) obj;
        return Objects.equals(checksum, other.checksum) && Arrays.equals(content, other.content)
                && Objects.equals(contentType, other.contentType) && Objects.equals(name, other.name);
    }

}
