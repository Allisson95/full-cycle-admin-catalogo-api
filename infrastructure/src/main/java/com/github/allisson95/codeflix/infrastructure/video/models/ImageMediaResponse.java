package com.github.allisson95.codeflix.infrastructure.video.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ImageMediaResponse(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("location") String location,
        @JsonProperty("checksum") String checksum) {

}
