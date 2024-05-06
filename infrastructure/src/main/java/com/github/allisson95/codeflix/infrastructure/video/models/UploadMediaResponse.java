package com.github.allisson95.codeflix.infrastructure.video.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.allisson95.codeflix.domain.video.VideoMediaType;

public record UploadMediaResponse(
        @JsonProperty("video_id") String videoId,
        @JsonProperty("media_type") VideoMediaType mediaType) {

}
