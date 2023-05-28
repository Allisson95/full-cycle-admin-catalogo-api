package com.github.allisson95.codeflix.application.castmember.create;

import com.github.allisson95.codeflix.domain.castmember.CastMemberType;

public record CreateCastMemberCommand(
        String name,
        CastMemberType type) {

    public static CreateCastMemberCommand with(
            final String aName,
            final CastMemberType aType) {
        return new CreateCastMemberCommand(aName, aType);
    }

}
