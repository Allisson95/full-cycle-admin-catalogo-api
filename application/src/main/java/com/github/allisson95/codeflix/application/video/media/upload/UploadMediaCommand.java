package com.github.allisson95.codeflix.application.video.media.upload;

import com.github.allisson95.codeflix.domain.video.VideoResource;

public record UploadMediaCommand(
        String videoId,
        VideoResource videoResource) {

    public static UploadMediaCommand with(final String videoId, final VideoResource videoResource) {
        return new UploadMediaCommand(videoId, videoResource);
    }

}
