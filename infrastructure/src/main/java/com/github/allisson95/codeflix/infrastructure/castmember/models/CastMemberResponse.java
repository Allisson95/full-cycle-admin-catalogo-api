package com.github.allisson95.codeflix.infrastructure.castmember.models;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.allisson95.codeflix.domain.castmember.CastMemberType;

public record CastMemberResponse(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("type") CastMemberType type,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt) {

}
