package com.github.allisson95.codeflix.application.video.media.update;

import com.github.allisson95.codeflix.domain.video.MediaStatus;

public record UpdateMediaStatusCommand(
        MediaStatus status,
        String videoId,
        String resourceId,
        String folder,
        String filename) {

    public static UpdateMediaStatusCommand with(
            final MediaStatus status,
            final String videoId,
            final String resourceId,
            final String folder,
            final String filename) {
        return new UpdateMediaStatusCommand(status, videoId, resourceId, folder, filename);
    }

}
