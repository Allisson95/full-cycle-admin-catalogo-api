package com.github.allisson95.codeflix.domain.video;

import java.util.Objects;

import com.github.allisson95.codeflix.domain.ValueObject;

public class ImageMedia extends ValueObject {

    private final String checksum;
    private final String name;
    private final String location;

    private ImageMedia(
            final String checksum,
            final String name,
            final String location) {
        this.checksum = Objects.requireNonNull(checksum);
        this.name = Objects.requireNonNull(name);
        this.location = Objects.requireNonNull(location);
    }

    public static ImageMedia with(
            final String checksum,
            final String name,
            final String location) {
        return new ImageMedia(checksum, name, location);
    }

    public String checksum() {
        return checksum;
    }

    public String name() {
        return name;
    }

    public String location() {
        return location;
    }

    @Override
    public int hashCode() {
        return Objects.hash(checksum, location);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ImageMedia other = (ImageMedia) obj;
        return Objects.equals(checksum, other.checksum) && Objects.equals(location, other.location);
    }

}
