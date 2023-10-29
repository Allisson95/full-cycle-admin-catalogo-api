package com.github.allisson95.codeflix.domain.video;

import java.util.Objects;

import com.github.allisson95.codeflix.domain.ValueObject;

public class VideoMedia extends ValueObject {

    private final String checksum;
    private final String name;
    private final String rawLocation;
    private final String encodedLocation;
    private final MediaStatus status;

    private VideoMedia(
            final String checksum,
            final String name,
            final String rawLocation,
            final String encodedLocation,
            final MediaStatus status) {
        this.checksum = Objects.requireNonNull(checksum);
        this.name = Objects.requireNonNull(name);
        this.rawLocation = Objects.requireNonNull(rawLocation);
        this.encodedLocation = Objects.requireNonNull(encodedLocation);
        this.status = Objects.requireNonNull(status);
    }

    public static VideoMedia with(
            final String checksum,
            final String name,
            final String rawLocation,
            final String encodedLocation,
            final MediaStatus status) {
        return new VideoMedia(checksum, name, rawLocation, encodedLocation, status);
    }

    public String checksum() {
        return checksum;
    }

    public String name() {
        return name;
    }

    public String rawLocation() {
        return rawLocation;
    }

    public String encodedLocation() {
        return encodedLocation;
    }

    public MediaStatus status() {
        return status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(checksum, rawLocation);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        VideoMedia other = (VideoMedia) obj;
        return Objects.equals(checksum, other.checksum) && Objects.equals(rawLocation, other.rawLocation);
    }

}
