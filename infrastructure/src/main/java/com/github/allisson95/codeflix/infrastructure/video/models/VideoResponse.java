package com.github.allisson95.codeflix.infrastructure.video.models;

import java.time.Instant;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VideoResponse(
        @JsonProperty("id") String id,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("year_launched") int yearLaunched,
        @JsonProperty("duration") double duration,
        @JsonProperty("rating") String rating,
        @JsonProperty("opened") boolean opened,
        @JsonProperty("published") boolean published,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt,
        @JsonProperty("banner") ImageMediaResponse banner,
        @JsonProperty("thumbnail") ImageMediaResponse thumbnail,
        @JsonProperty("thumbnail_half") ImageMediaResponse thumbnailHalf,
        @JsonProperty("trailer") VideoMediaResponse trailer,
        @JsonProperty("video") VideoMediaResponse video,
        @JsonProperty("categories_id") Set<String> categoriesId,
        @JsonProperty("genres_id") Set<String> genresId,
        @JsonProperty("cast_members_id") Set<String> castMembersId) {

}
