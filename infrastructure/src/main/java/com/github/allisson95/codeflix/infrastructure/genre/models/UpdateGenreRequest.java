package com.github.allisson95.codeflix.infrastructure.genre.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateGenreRequest(
        @JsonProperty("name") String name,
        @JsonProperty("categories_id") List<String> categories,
        @JsonProperty("is_active") Boolean active) {

}
