package com.github.allisson95.codeflix.domain.video;

import java.util.Objects;

import com.github.allisson95.codeflix.domain.ValueObject;
import com.github.allisson95.codeflix.domain.resource.Resource;

public class VideoResource extends ValueObject {

    private final Resource resource;
    private final VideoMediaType type;

    private VideoResource(final Resource resource, final VideoMediaType type) {
        this.resource = Objects.requireNonNull(resource);
        this.type = Objects.requireNonNull(type);
    }

    public static VideoResource with(final Resource resource, final VideoMediaType type) {
        return new VideoResource(resource, type);
    }

    public Resource getResource() {
        return resource;
    }

    public VideoMediaType getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        VideoResource other = (VideoResource) obj;
        return type == other.type;
    }

}
