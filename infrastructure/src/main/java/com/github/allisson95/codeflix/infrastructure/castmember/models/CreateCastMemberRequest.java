package com.github.allisson95.codeflix.infrastructure.castmember.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.allisson95.codeflix.domain.castmember.CastMemberType;

public record CreateCastMemberRequest(
        @JsonProperty("name") String name,
        @JsonProperty("type") CastMemberType type) {

}
