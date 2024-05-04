package com.github.allisson95.codeflix.infrastructure.video.models;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateVideoRequest(
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("year_launched") Integer yearLaunched,
        @JsonProperty("duration") Double duration,
        @JsonProperty("rating") String rating,
        @JsonProperty("opened") Boolean opened,
        @JsonProperty("published") Boolean published,
        @JsonProperty("categories") Set<String> categories,
        @JsonProperty("genres") Set<String> genres,
        @JsonProperty("cast_members") Set<String> castMembers) {

}
