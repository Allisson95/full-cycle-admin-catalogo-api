package com.github.allisson95.codeflix.application.video.delete;

import java.util.Objects;

import com.github.allisson95.codeflix.domain.video.VideoGateway;
import com.github.allisson95.codeflix.domain.video.VideoID;

public class DefaultDeleteVideoUseCase extends DeleteVideoUseCase {

    private final VideoGateway videoGateway;

    public DefaultDeleteVideoUseCase(final VideoGateway videoGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public void execute(final String anId) {
        final var videoId = VideoID.from(anId);
        this.videoGateway.deleteById(videoId);
    }

}
