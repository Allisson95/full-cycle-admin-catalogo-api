package com.github.allisson95.codeflix.application.castmember.retrieve.get;

import java.time.Instant;

import com.github.allisson95.codeflix.domain.castmember.CastMember;
import com.github.allisson95.codeflix.domain.castmember.CastMemberID;
import com.github.allisson95.codeflix.domain.castmember.CastMemberType;

public record CastMemberOutput(
        CastMemberID id,
        String name,
        CastMemberType type,
        Instant createdAt,
        Instant updatedAt) {

    public static CastMemberOutput with(final CastMember aMember) {
        return new CastMemberOutput(
                aMember.getId(),
                aMember.getName(),
                aMember.getType(),
                aMember.getCreatedAt(),
                aMember.getUpdatedAt());
    }

}
