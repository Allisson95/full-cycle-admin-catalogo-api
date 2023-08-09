package com.github.allisson95.codeflix.infrastructure.castmember.models;

import com.github.allisson95.codeflix.domain.castmember.CastMemberType;

public record CreateCastMemberRequest(
        String name,
        CastMemberType type) {

}
