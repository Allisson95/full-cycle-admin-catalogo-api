package com.github.allisson95.codeflix.application.castmember.create;

import com.github.allisson95.codeflix.domain.castmember.CastMember;

public record CreateCastMemberOutput(
    String id
) {

    public static CreateCastMemberOutput with(final CastMember aMember) {
        return new CreateCastMemberOutput(aMember.getId().getValue());
    }

}
