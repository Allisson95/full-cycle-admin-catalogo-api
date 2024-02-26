package com.github.allisson95.codeflix.application.video.update;

import com.github.allisson95.codeflix.domain.video.Video;

public record UpdateVideoOutput(String id) {

    public static UpdateVideoOutput from(final Video aVideo) {
        return new UpdateVideoOutput(aVideo.getId().getValue());
    }

}
