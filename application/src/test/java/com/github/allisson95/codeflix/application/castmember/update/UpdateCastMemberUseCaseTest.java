package com.github.allisson95.codeflix.application.castmember.update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.github.allisson95.codeflix.application.Fixture;
import com.github.allisson95.codeflix.application.UseCaseTest;
import com.github.allisson95.codeflix.domain.castmember.CastMember;
import com.github.allisson95.codeflix.domain.castmember.CastMemberGateway;
import com.github.allisson95.codeflix.domain.castmember.CastMemberID;
import com.github.allisson95.codeflix.domain.castmember.CastMemberType;
import com.github.allisson95.codeflix.domain.exceptions.NotFoundException;
import com.github.allisson95.codeflix.domain.exceptions.NotificationException;

class UpdateCastMemberUseCaseTest extends UseCaseTest {

    @Mock
    private CastMemberGateway castMemberGateway;

    @InjectMocks
    private DefaultUpdateCastMemberUseCase useCase;

    @Override
    protected List<Object> getMocksForClean() {
        return List.of(castMemberGateway);
    }

    @Test
    void Given_AValidCommand_When_CallsUpdateCastMember_Should_ReturnItsIdentifier() {
        final var aMember = CastMember.newMember("invalid name", CastMemberType.DIRECTOR);

        final var expectedId = aMember.getId();
        final var expectedName = Fixture.name();
        final var expectedType = CastMemberType.ACTOR;

        final var aCommand = UpdateCastMemberCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedType);

        when(castMemberGateway.findById(expectedId))
                .thenReturn(Optional.of(CastMember.with(aMember)));

        when(castMemberGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var actualOutput = useCase.execute(aCommand);

        assertNotNull(actualOutput);
        assertEquals(expectedId.getValue(), actualOutput.id());

        verify(castMemberGateway).findById(expectedId);

        verify(castMemberGateway).update(argThat(aUpdatedMember -> Objects.equals(expectedId, aUpdatedMember.getId())
                && Objects.equals(expectedName, aUpdatedMember.getName())
                && Objects.equals(expectedType, aUpdatedMember.getType())
                && Objects.equals(aMember.getCreatedAt(), aUpdatedMember.getCreatedAt())
                && aMember.getUpdatedAt().isBefore(aUpdatedMember.getUpdatedAt())));
    }

    @Test
    void Given_AInvalidNullName_When_CallsUpdateCastMember_Should_ThrowsNotificationException() {
        final var aMember = CastMember.newMember(Fixture.name(), CastMemberType.DIRECTOR);

        final var expectedId = aMember.getId();
        final String expectedName = null;
        final var expectedType = CastMemberType.ACTOR;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var aCommand = UpdateCastMemberCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedType);

        when(castMemberGateway.findById(expectedId))
                .thenReturn(Optional.of(aMember));

        final var actualException = assertThrows(
                NotificationException.class,
                () -> useCase.execute(aCommand));

        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(castMemberGateway).findById(expectedId);
        verify(castMemberGateway, times(0)).update(any());
    }

    @Test
    void Given_AInvalidType_When_CallsUpdateCastMember_Should_ThrowsNotificationException() {
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());

        final var expectedId = aMember.getId();
        final var expectedName = Fixture.name();
        final CastMemberType expectedType = null;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'type' should not be null";

        final var aCommand = UpdateCastMemberCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedType);

        when(castMemberGateway.findById(expectedId))
                .thenReturn(Optional.of(aMember));

        final var actualException = assertThrows(
                NotificationException.class,
                () -> useCase.execute(aCommand));

        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(castMemberGateway).findById(expectedId);
        verify(castMemberGateway, times(0)).update(any());
    }

    @Test
    void Given_AInvalidId_When_CallsUpdateCastMember_Should_ThrowsNotFoundException() {
        final var expectedId = CastMemberID.from("123");
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();
        final var expectedErrorMessage = "CastMember with id 123 was not found";

        final var aCommand = UpdateCastMemberCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedType);

        when(castMemberGateway.findById(expectedId))
                .thenReturn(Optional.empty());

        final var actualException = assertThrows(
                NotFoundException.class,
                () -> useCase.execute(aCommand));

        assertNotNull(actualException);
        assertEquals(expectedErrorMessage, actualException.getMessage());

        verify(castMemberGateway).findById(expectedId);
        verify(castMemberGateway, times(0)).update(any());
    }

}
