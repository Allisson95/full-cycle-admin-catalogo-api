package com.github.allisson95.codeflix.infrastructure.video;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.github.allisson95.codeflix.domain.resource.Resource;
import com.github.allisson95.codeflix.domain.video.ImageMedia;
import com.github.allisson95.codeflix.domain.video.MediaResourceGateway;
import com.github.allisson95.codeflix.domain.video.VideoID;
import com.github.allisson95.codeflix.domain.video.VideoMedia;
import com.github.allisson95.codeflix.domain.video.VideoMediaType;
import com.github.allisson95.codeflix.domain.video.VideoResource;
import com.github.allisson95.codeflix.infrastructure.configuration.properties.storage.StorageProperties;
import com.github.allisson95.codeflix.infrastructure.services.StorageService;

@Component
public class DefaultMediaResourceGateway implements MediaResourceGateway {

    private final String filenamePattern;
    private final String locationPattern;
    private final StorageService storageService;

    public DefaultMediaResourceGateway(
            final StorageProperties storageProperties,
            final StorageService storageService) {
        this.filenamePattern = storageProperties.getFilenamePattern();
        this.locationPattern = storageProperties.getLocationPattern();
        this.storageService = Objects.requireNonNull(storageService);
    }

    @Override
    public VideoMedia storeVideo(final VideoID anId, final VideoResource videoResource) {
        final var filepath = filepath(anId, videoResource.getType());
        final var aResource = videoResource.getResource();
        store(filepath, aResource);
        return VideoMedia.with(aResource.checksum(), aResource.name(), filepath);
    }

    @Override
    public ImageMedia storeImage(final VideoID anId, final VideoResource videoResource) {
        final var filepath = filepath(anId, videoResource.getType());
        final var aResource = videoResource.getResource();
        store(filepath, aResource);
        return ImageMedia.with(aResource.checksum(), aResource.name(), filepath);
    }

    @Override
    public Optional<Resource> getResource(VideoID anId, VideoMediaType aType) {
        return this.storageService.get(filepath(anId, aType));
    }

    @Override
    public void clearResources(final VideoID anId) {
        final var ids = this.storageService.list(folder(anId));
        this.storageService.deleteAll(ids);
    }

    private String filename(final VideoMediaType aType) {
        return filenamePattern.replace("{type}", aType.name());
    }

    private String folder(final VideoID anId) {
        return locationPattern.replace("{videoId}", anId.getValue());
    }

    private String filepath(final VideoID anId, final VideoMediaType aType) {
        return folder(anId)
                .concat("/")
                .concat(filename(aType));
    }

    private void store(final String filepath, final Resource aResource) {
        this.storageService.store(filepath, aResource);
    }

}
