package com.github.allisson95.codeflix.infrastructure.castmember.models;

import java.time.Instant;

import com.github.allisson95.codeflix.domain.castmember.CastMemberType;

public record CastMemberListResponse(
        String id,
        String name,
        CastMemberType type,
        Instant createdAt) {

}
