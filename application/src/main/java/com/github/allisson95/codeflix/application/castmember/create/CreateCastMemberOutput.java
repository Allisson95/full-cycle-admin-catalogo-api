package com.github.allisson95.codeflix.application.castmember.create;

import com.github.allisson95.codeflix.domain.castmember.CastMember;
import com.github.allisson95.codeflix.domain.castmember.CastMemberID;

public record CreateCastMemberOutput(
    String id
) {

    public static CreateCastMemberOutput with(final CastMemberID anId) {
        return new CreateCastMemberOutput(anId.getValue());
    }

    public static CreateCastMemberOutput with(final CastMember aMember) {
        return with(aMember.getId());
    }

}
