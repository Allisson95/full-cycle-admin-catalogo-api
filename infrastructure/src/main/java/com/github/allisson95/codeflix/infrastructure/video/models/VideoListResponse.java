package com.github.allisson95.codeflix.infrastructure.video.models;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VideoListResponse(
        @JsonProperty("id") String id,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt) {

}
