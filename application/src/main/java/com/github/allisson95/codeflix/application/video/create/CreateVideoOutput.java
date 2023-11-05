package com.github.allisson95.codeflix.application.video.create;

import com.github.allisson95.codeflix.domain.video.Video;

public record CreateVideoOutput(String id) {

    public static CreateVideoOutput from(final Video aVideo) {
        return new CreateVideoOutput(aVideo.getId().getValue());
    }

}
