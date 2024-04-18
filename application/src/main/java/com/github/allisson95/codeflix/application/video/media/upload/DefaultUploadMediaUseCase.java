package com.github.allisson95.codeflix.application.video.media.upload;

import java.util.Objects;
import java.util.function.Supplier;

import com.github.allisson95.codeflix.domain.Identifier;
import com.github.allisson95.codeflix.domain.exceptions.DomainException;
import com.github.allisson95.codeflix.domain.exceptions.NotFoundException;
import com.github.allisson95.codeflix.domain.video.MediaResourceGateway;
import com.github.allisson95.codeflix.domain.video.Video;
import com.github.allisson95.codeflix.domain.video.VideoGateway;
import com.github.allisson95.codeflix.domain.video.VideoID;

public class DefaultUploadMediaUseCase extends UploadMediaUseCase {

    private final VideoGateway videoGateway;
    private final MediaResourceGateway mediaResourceGateway;

    public DefaultUploadMediaUseCase(final VideoGateway videoGateway, final MediaResourceGateway mediaResourceGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
    }

    @Override
    public UploadMediaOutput execute(final UploadMediaCommand aCommand) {
        final var anId = VideoID.from(aCommand.videoId());
        final var aResource = aCommand.videoResource();

        final var aVideo = this.videoGateway.findById(anId)
                .orElseThrow(notFound(anId));

        switch (aResource.getType()) {
            case BANNER:
                aVideo.setBanner(this.mediaResourceGateway.storeImage(anId, aResource));
                break;
            case THUMBNAIL:
                aVideo.setThumbnail(this.mediaResourceGateway.storeImage(anId, aResource));
                break;
            case THUMBNAIL_HALF:
                aVideo.setThumbnailHalf(this.mediaResourceGateway.storeImage(anId, aResource));
                break;
            case TRAILER:
                aVideo.setTrailer(this.mediaResourceGateway.storeVideo(anId, aResource));
                break;
            case VIDEO:
                aVideo.setVideo(this.mediaResourceGateway.storeVideo(anId, aResource));
                break;

            default:
                break;
        }

        return UploadMediaOutput.with(this.videoGateway.update(aVideo), aResource.getType());
    }

    private Supplier<? extends DomainException> notFound(final Identifier anId) {
        return () -> NotFoundException.with(Video.class, anId);
    }

}
