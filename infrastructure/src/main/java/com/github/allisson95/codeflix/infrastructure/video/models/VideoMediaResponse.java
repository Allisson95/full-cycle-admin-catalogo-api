package com.github.allisson95.codeflix.infrastructure.video.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VideoMediaResponse(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("checksum") String checksum,
        @JsonProperty("location") String rawLocation,
        @JsonProperty("encoded_location") String encodedLocation,
        @JsonProperty("status") String status) {

}
