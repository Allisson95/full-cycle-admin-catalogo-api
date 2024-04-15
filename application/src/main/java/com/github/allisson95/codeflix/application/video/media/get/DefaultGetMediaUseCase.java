package com.github.allisson95.codeflix.application.video.media.get;

import java.util.Objects;
import java.util.function.Supplier;

import com.github.allisson95.codeflix.domain.exceptions.NotFoundException;
import com.github.allisson95.codeflix.domain.validation.Error;
import com.github.allisson95.codeflix.domain.video.MediaResourceGateway;
import com.github.allisson95.codeflix.domain.video.VideoID;
import com.github.allisson95.codeflix.domain.video.VideoMediaType;

public class DefaultGetMediaUseCase extends GetMediaUseCase {

    private final MediaResourceGateway mediaResourceGateway;

    public DefaultGetMediaUseCase(final MediaResourceGateway mediaResourceGateway) {
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
    }

    @Override
    public MediaOutput execute(final GetMediaCommand aCommand) {
        final var anId = VideoID.from(aCommand.videoId());
        final var aType = VideoMediaType.of(aCommand.mediaType())
                .orElseThrow(typeNotFound(aCommand.mediaType()));

        final var aResource = this.mediaResourceGateway.getResource(anId, aType)
                .orElseThrow(notFound(anId, aType));

        return MediaOutput.with(aResource);
    }

    private Supplier<? extends NotFoundException> typeNotFound(final String aType) {
        return () -> NotFoundException
                .with(new Error("Media type %s doesn't exists".formatted(aType)));
    }

    private Supplier<? extends NotFoundException> notFound(final VideoID anId, final VideoMediaType aType) {
        return () -> NotFoundException
                .with(new Error("Resource %s not found for video %s".formatted(aType.name(), anId.getValue())));
    }

}
