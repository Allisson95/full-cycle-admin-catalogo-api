package com.github.allisson95.codeflix.domain.video;

public interface MediaResourceGateway {

    VideoMedia storeVideo(VideoID anId, VideoResource aResource);

    ImageMedia storeImage(VideoID anId, VideoResource aResource);

    void clearResources(VideoID anId);

}
