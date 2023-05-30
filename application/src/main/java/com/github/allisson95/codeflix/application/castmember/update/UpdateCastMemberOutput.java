package com.github.allisson95.codeflix.application.castmember.update;

import com.github.allisson95.codeflix.domain.castmember.CastMember;

public record UpdateCastMemberOutput(String id) {

    public static UpdateCastMemberOutput with(final CastMember aMember) {
        return new UpdateCastMemberOutput(aMember.getId().getValue());
    }

}
