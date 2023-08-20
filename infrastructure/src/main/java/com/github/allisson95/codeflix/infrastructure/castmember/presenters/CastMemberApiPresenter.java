package com.github.allisson95.codeflix.infrastructure.castmember.presenters;

import com.github.allisson95.codeflix.application.castmember.retrieve.get.CastMemberOutput;
import com.github.allisson95.codeflix.infrastructure.castmember.models.CastMemberResponse;

public interface CastMemberApiPresenter {

    static CastMemberResponse present(final CastMemberOutput output) {
        return new CastMemberResponse(
                output.id().getValue(),
                output.name(),
                output.type(),
                output.createdAt(),
                output.updatedAt());
    }

}
