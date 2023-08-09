package com.github.allisson95.codeflix.application.castmember.create;

import java.util.Objects;

import com.github.allisson95.codeflix.domain.castmember.CastMember;
import com.github.allisson95.codeflix.domain.castmember.CastMemberGateway;
import com.github.allisson95.codeflix.domain.exceptions.NotificationException;
import com.github.allisson95.codeflix.domain.validation.handler.Notification;

public non-sealed class DefaultCreateCastMemberUseCase extends CreateCastMemberUseCase {

    private final CastMemberGateway castMemberGateway;

    public DefaultCreateCastMemberUseCase(final CastMemberGateway castMemberGateway) {
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
    }

    @Override
    public CreateCastMemberOutput execute(final CreateCastMemberCommand aCommand) {
        final var aName = aCommand.name();
        final var aType = aCommand.type();

        final var notification = Notification.create();
        final var aMember = notification.validate(() -> CastMember.newMember(aName, aType));

        if (notification.hasError()) {
            notify(notification);
        }

        return CreateCastMemberOutput.with(this.castMemberGateway.create(aMember));
    }

    private void notify(final Notification notification) {
        throw new NotificationException("Could not create Aggregate CastMember", notification);
    }

}
