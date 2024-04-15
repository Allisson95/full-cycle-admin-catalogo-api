package com.github.allisson95.codeflix.domain.video;

import java.util.Arrays;
import java.util.Optional;

public enum VideoMediaType {
    BANNER,
    THUMBNAIL,
    THUMBNAIL_HALF,
    TRAILER,
    VIDEO;

    public static Optional<VideoMediaType> of(final String value) {
        return Arrays.stream(values())
                .filter(it -> it.name().equalsIgnoreCase(value))
                .findFirst();
    }

}