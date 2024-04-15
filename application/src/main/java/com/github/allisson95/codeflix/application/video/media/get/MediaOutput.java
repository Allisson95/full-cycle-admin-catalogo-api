package com.github.allisson95.codeflix.application.video.media.get;

import com.github.allisson95.codeflix.domain.resource.Resource;

public record MediaOutput(
        byte[] content,
        String contentType,
        String name) {

    public static MediaOutput with(final Resource aResource) {
        return new MediaOutput(
                aResource.content(),
                aResource.contentType(),
                aResource.name());
    }

}
