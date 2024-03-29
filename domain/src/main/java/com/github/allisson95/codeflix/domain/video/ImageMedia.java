package com.github.allisson95.codeflix.domain.video;

import java.util.Objects;

import com.github.allisson95.codeflix.domain.ValueObject;
import com.github.allisson95.codeflix.domain.utils.IdUtils;

public class ImageMedia extends ValueObject {

    private final String id;
    private final String checksum;
    private final String name;
    private final String location;

    private ImageMedia(
            final String id,
            final String checksum,
            final String name,
            final String location) {
        this.id = Objects.requireNonNull(id);
        this.checksum = Objects.requireNonNull(checksum);
        this.name = Objects.requireNonNull(name);
        this.location = Objects.requireNonNull(location);
    }

    public static ImageMedia with(
            final String id,
            final String checksum,
            final String name,
            final String location) {
        return new ImageMedia(id, checksum, name, location);
    }

    public static ImageMedia with(
            final String checksum,
            final String name,
            final String location) {
        return with(IdUtils.uuid(), checksum, name, location);
    }

    public String id() {
        return id;
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
