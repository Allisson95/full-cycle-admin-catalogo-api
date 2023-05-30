package com.github.allisson95.codeflix.application.castmember.update;

import java.util.Objects;
import java.util.function.Supplier;

import com.github.allisson95.codeflix.domain.Identifier;
import com.github.allisson95.codeflix.domain.castmember.CastMember;
import com.github.allisson95.codeflix.domain.castmember.CastMemberGateway;
import com.github.allisson95.codeflix.domain.castmember.CastMemberID;
import com.github.allisson95.codeflix.domain.exceptions.DomainException;
import com.github.allisson95.codeflix.domain.exceptions.NotFoundException;
import com.github.allisson95.codeflix.domain.exceptions.NotificationException;
import com.github.allisson95.codeflix.domain.validation.handler.Notification;

public final class DefaultUpdateCastMemberUseCase extends UpdateCastMemberUseCase {

    private final CastMemberGateway castMemberGateway;

    public DefaultUpdateCastMemberUseCase(final CastMemberGateway castMemberGateway) {
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
    }

    @Override
    public UpdateCastMemberOutput execute(final UpdateCastMemberCommand aCommand) {
        final var anId = CastMemberID.from(aCommand.id());
        final var aName = aCommand.name();
        final var aType = aCommand.type();

        final var aMember = this.castMemberGateway.findById(anId)
                .orElseThrow(notFound(anId));

        final var notification = Notification.create();
        final var updatedMember = notification.validate(() -> aMember.update(aName, aType));

        if (notification.hasError()) {
            notify(anId, notification);
        }

        return UpdateCastMemberOutput.with(this.castMemberGateway.update(updatedMember));
    }

    private Supplier<? extends DomainException> notFound(final Identifier anId) {
        return () -> NotFoundException.with(CastMember.class, anId);
    }

    private void notify(final Identifier anId, final Notification notification) {
        throw new NotificationException(
                "Could not update Aggregate CastMember %s".formatted(anId.getValue()),
                notification);
    }

}
