package com.github.allisson95.codeflix.domain.video;

public interface MediaResourceGateway {

    VideoMedia storeVideo(VideoID anId, Resource aResource);

    ImageMedia storeImage(VideoID anId, Resource aResource);

    void clearResources(VideoID anId);

}
