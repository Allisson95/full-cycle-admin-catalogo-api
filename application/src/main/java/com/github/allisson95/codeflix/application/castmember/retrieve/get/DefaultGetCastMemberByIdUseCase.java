package com.github.allisson95.codeflix.application.castmember.retrieve.get;

import java.util.Objects;
import java.util.function.Supplier;

import com.github.allisson95.codeflix.domain.Identifier;
import com.github.allisson95.codeflix.domain.castmember.CastMember;
import com.github.allisson95.codeflix.domain.castmember.CastMemberGateway;
import com.github.allisson95.codeflix.domain.castmember.CastMemberID;
import com.github.allisson95.codeflix.domain.exceptions.DomainException;
import com.github.allisson95.codeflix.domain.exceptions.NotFoundException;

public final class DefaultGetCastMemberByIdUseCase extends GetCastMemberByIdUseCase {

    private final CastMemberGateway castMemberGateway;

    public DefaultGetCastMemberByIdUseCase(final CastMemberGateway castMemberGateway) {
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
    }

    @Override
    public CastMemberOutput execute(final String anId) {
        final var aMemberId = CastMemberID.from(anId);
        return this.castMemberGateway.findById(aMemberId)
                .map(CastMemberOutput::with)
                .orElseThrow(notFound(aMemberId));
    }

    private Supplier<? extends DomainException> notFound(final Identifier anId) {
        return () -> NotFoundException.with(CastMember.class, anId);
    }

}
