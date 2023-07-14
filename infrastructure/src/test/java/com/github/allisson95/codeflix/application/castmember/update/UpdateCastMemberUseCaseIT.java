package com.github.allisson95.codeflix.application.castmember.update;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.github.allisson95.codeflix.Fixture;
import com.github.allisson95.codeflix.IntegrationTest;
import com.github.allisson95.codeflix.domain.castmember.CastMember;
import com.github.allisson95.codeflix.domain.castmember.CastMemberGateway;
import com.github.allisson95.codeflix.domain.castmember.CastMemberID;
import com.github.allisson95.codeflix.domain.castmember.CastMemberType;
import com.github.allisson95.codeflix.domain.exceptions.NotFoundException;
import com.github.allisson95.codeflix.domain.exceptions.NotificationException;
import com.github.allisson95.codeflix.infrastructure.castmember.persistence.CastMemberJpaEntity;
import com.github.allisson95.codeflix.infrastructure.castmember.persistence.CastMemberRepository;

@IntegrationTest
class UpdateCastMemberUseCaseIT {

    @Autowired
    private CastMemberRepository castMemberRepository;

    @SpyBean
    private CastMemberGateway castMemberGateway;

    @Autowired
    private UpdateCastMemberUseCase useCase;

    @Test
    void Given_AValidCommand_When_CallsUpdateCastMember_Should_ReturnItsIdentifier() {
        final var aMember = CastMember.newMember("invalid name", CastMemberType.DIRECTOR);
        this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));

        final var expectedId = aMember.getId();
        final var expectedName = Fixture.name();
        final var expectedType = CastMemberType.ACTOR;

        final var aCommand = UpdateCastMemberCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedType);

        assertEquals(1, this.castMemberRepository.count());

        final var actualOutput = useCase.execute(aCommand);

        assertEquals(1, this.castMemberRepository.count());

        assertNotNull(actualOutput);
        assertEquals(expectedId.getValue(), actualOutput.id());

        final var updatedMember = this.castMemberRepository.findById(expectedId.getValue()).get();

        assertEquals(expectedName, updatedMember.getName());
        assertEquals(expectedType, updatedMember.getType());
        assertEquals(aMember.getCreatedAt(), updatedMember.getCreatedAt());
        assertTrue(aMember.getUpdatedAt().isBefore(updatedMember.getUpdatedAt()));

        verify(castMemberGateway).findById(any());
        verify(castMemberGateway).update(any());
    }

    @Test
    void Given_AInvalidNullName_When_CallsUpdateCastMember_Should_ThrowsNotificationException() {
        final var aMember = CastMember.newMember(Fixture.name(), CastMemberType.DIRECTOR);
        this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));

        final var expectedId = aMember.getId();
        final String expectedName = null;
        final var expectedType = CastMemberType.ACTOR;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var aCommand = UpdateCastMemberCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedType);

        assertEquals(1, this.castMemberRepository.count());

        final var actualException = assertThrows(
                NotificationException.class,
                () -> useCase.execute(aCommand));

        assertEquals(1, this.castMemberRepository.count());

        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(castMemberGateway).findById(expectedId);
        verify(castMemberGateway, times(0)).update(any());
    }

    @Test
    void Given_AInvalidType_When_CallsUpdateCastMember_Should_ThrowsNotificationException() {
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMember.type());
        this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));

        final var expectedId = aMember.getId();
        final var expectedName = Fixture.name();
        final CastMemberType expectedType = null;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'type' should not be null";

        final var aCommand = UpdateCastMemberCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedType);

        assertEquals(1, this.castMemberRepository.count());

        final var actualException = assertThrows(
                NotificationException.class,
                () -> useCase.execute(aCommand));

        assertEquals(1, this.castMemberRepository.count());

        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(castMemberGateway).findById(expectedId);
        verify(castMemberGateway, times(0)).update(any());
    }

    @Test
    void Given_AInvalidId_When_CallsUpdateCastMember_Should_ThrowsNotFoundException() {
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMember.type());
        this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));

        final var expectedId = CastMemberID.from("123");
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();
        final var expectedErrorMessage = "CastMember with id 123 was not found";

        final var aCommand = UpdateCastMemberCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedType);

        assertEquals(1, this.castMemberRepository.count());

        final var actualException = assertThrows(
                NotFoundException.class,
                () -> useCase.execute(aCommand));

        assertEquals(1, this.castMemberRepository.count());

        assertNotNull(actualException);
        assertEquals(expectedErrorMessage, actualException.getMessage());

        verify(castMemberGateway).findById(expectedId);
        verify(castMemberGateway, times(0)).update(any());
    }

}
