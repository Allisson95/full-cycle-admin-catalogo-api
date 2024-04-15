package com.github.allisson95.codeflix.domain.video;

import java.util.Optional;

import com.github.allisson95.codeflix.domain.resource.Resource;

public interface MediaResourceGateway {

    VideoMedia storeVideo(VideoID anId, VideoResource aResource);

    ImageMedia storeImage(VideoID anId, VideoResource aResource);

    Optional<Resource> getResource(VideoID anId, VideoMediaType aType);

    void clearResources(VideoID anId);

}
