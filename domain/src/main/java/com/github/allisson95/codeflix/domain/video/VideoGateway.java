package com.github.allisson95.codeflix.domain.video;

import java.util.Optional;

import com.github.allisson95.codeflix.domain.pagination.Pagination;

public interface VideoGateway {

    Video create(Video aVideo);

    void deleteById(VideoID anId);

    Optional<Video> findById(VideoID anId);

    Pagination<VideoPreview> findAll(VideoSearchQuery aQuery);

    Video update(Video aVideo);

}
