package com.github.allisson95.codeflix.application.castmember.retrieve.list;

import java.time.Instant;

import com.github.allisson95.codeflix.domain.castmember.CastMember;
import com.github.allisson95.codeflix.domain.castmember.CastMemberType;

public record CastMemberListOutput(
        String id,
        String name,
        CastMemberType type,
        Instant createdAt) {

    public static CastMemberListOutput from(final CastMember aMember) {
        return new CastMemberListOutput(
                aMember.getId().getValue(),
                aMember.getName(),
                aMember.getType(),
                aMember.getCreatedAt());
    }

}
