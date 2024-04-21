package com.github.allisson95.codeflix.application.video.delete;

import java.util.Objects;

import com.github.allisson95.codeflix.domain.video.MediaResourceGateway;
import com.github.allisson95.codeflix.domain.video.VideoGateway;
import com.github.allisson95.codeflix.domain.video.VideoID;

public class DefaultDeleteVideoUseCase extends DeleteVideoUseCase {

    private final VideoGateway videoGateway;
    private final MediaResourceGateway mediaResourceGateway;

    public DefaultDeleteVideoUseCase(
            final VideoGateway videoGateway,
            final MediaResourceGateway mediaResourceGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
    }

    @Override
    public void execute(final String anId) {
        final var videoId = VideoID.from(anId);
        this.videoGateway.deleteById(videoId);
        this.mediaResourceGateway.clearResources(videoId);
    }

}
