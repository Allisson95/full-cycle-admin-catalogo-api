package com.github.allisson95.codeflix.application.video.media.update;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.github.allisson95.codeflix.domain.Identifier;
import com.github.allisson95.codeflix.domain.exceptions.DomainException;
import com.github.allisson95.codeflix.domain.exceptions.NotFoundException;
import com.github.allisson95.codeflix.domain.video.MediaStatus;
import com.github.allisson95.codeflix.domain.video.Video;
import com.github.allisson95.codeflix.domain.video.VideoGateway;
import com.github.allisson95.codeflix.domain.video.VideoID;
import com.github.allisson95.codeflix.domain.video.VideoMedia;
import com.github.allisson95.codeflix.domain.video.VideoMediaType;

public class DefaultUpdateMediaStatusUseCase extends UpdateMediaStatusUseCase {

    private final VideoGateway videoGateway;

    public DefaultUpdateMediaStatusUseCase(final VideoGateway videoGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public void execute(final UpdateMediaStatusCommand aCommand) {
        final var anId = VideoID.from(aCommand.videoId());
        final var aResourceId = aCommand.resourceId();
        final var folder = aCommand.folder();
        final var filename = aCommand.filename();

        final var aVideo = this.videoGateway.findById(anId)
                .orElseThrow(notFound(anId));

        final var encodedPath = "%s/%s".formatted(folder, filename);

        final Predicate<VideoMedia> equals = it -> aResourceId.equals(it.id());

        aVideo.getTrailer()
                .filter(equals)
                .ifPresent(updateVideoMedia(VideoMediaType.TRAILER, aCommand.status(), aVideo, encodedPath));

        aVideo.getVideo()
                .filter(equals)
                .ifPresent(updateVideoMedia(VideoMediaType.VIDEO, aCommand.status(), aVideo, encodedPath));
    }

    private Consumer<VideoMedia> updateVideoMedia(
            final VideoMediaType aType,
            final MediaStatus status,
            final Video aVideo,
            final String encodedPath) {
        return it -> {
            switch (status) {
                case PROCESSING:
                    aVideo.processing(aType);
                    break;
                case COMPLETED:
                    aVideo.completed(aType, encodedPath);
                    break;

                default:
                    break;
            }

            this.videoGateway.update(aVideo);
        };
    }

    private Supplier<? extends DomainException> notFound(final Identifier anId) {
        return () -> NotFoundException.with(Video.class, anId);
    }

}
