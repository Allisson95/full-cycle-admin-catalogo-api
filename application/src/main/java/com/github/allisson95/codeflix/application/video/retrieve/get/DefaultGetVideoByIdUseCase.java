package com.github.allisson95.codeflix.application.video.retrieve.get;

import java.util.Objects;
import java.util.function.Supplier;

import com.github.allisson95.codeflix.domain.Identifier;
import com.github.allisson95.codeflix.domain.exceptions.NotFoundException;
import com.github.allisson95.codeflix.domain.video.Video;
import com.github.allisson95.codeflix.domain.video.VideoGateway;
import com.github.allisson95.codeflix.domain.video.VideoID;

public class DefaultGetVideoByIdUseCase extends GetVideoByIdUseCase {

    private final VideoGateway videoGateway;

    public DefaultGetVideoByIdUseCase(final VideoGateway videoGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public VideoOutput execute(final String anId) {
        final var videoId = VideoID.from(anId);
        return this.videoGateway
                .findById(videoId)
                .map(VideoOutput::from)
                .orElseThrow(notFound(videoId));
    }

    private Supplier<? extends NotFoundException> notFound(final Identifier id) {
        return () -> NotFoundException.with(Video.class, id);
    }

}
