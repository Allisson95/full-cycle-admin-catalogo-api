package com.github.allisson95.codeflix.application.castmember.retrieve.get;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.github.allisson95.codeflix.Fixture;
import com.github.allisson95.codeflix.IntegrationTest;
import com.github.allisson95.codeflix.domain.castmember.CastMember;
import com.github.allisson95.codeflix.domain.castmember.CastMemberGateway;
import com.github.allisson95.codeflix.domain.castmember.CastMemberID;
import com.github.allisson95.codeflix.domain.exceptions.NotFoundException;
import com.github.allisson95.codeflix.infrastructure.castmember.persistence.CastMemberJpaEntity;
import com.github.allisson95.codeflix.infrastructure.castmember.persistence.CastMemberRepository;

@IntegrationTest
class GetCastMemberByIdUseCaseIT {

    @Autowired
    private CastMemberRepository castMemberRepository;

    @SpyBean
    private CastMemberGateway castMemberGateway;

    @Autowired
    private GetCastMemberByIdUseCase useCase;

    @Test
    void Given_AValidAndExistingId_When_CallsGetCastMemberById_Should_ReturnCastMember() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();
        final var aMember = CastMember.newMember(expectedName, expectedType);
        this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));
        final var expectedId = aMember.getId();

        final var actualOutput = useCase.execute(expectedId.getValue());

        assertNotNull(actualOutput);
        assertEquals(expectedId, actualOutput.id());
        assertEquals(expectedName, actualOutput.name());
        assertEquals(expectedType, actualOutput.type());
        assertEquals(aMember.getCreatedAt(), actualOutput.createdAt());
        assertEquals(aMember.getUpdatedAt(), actualOutput.updatedAt());

        verify(castMemberGateway).findById(any());
    }

    @Test
    void Given_AInvalidAndInexistingId_When_CallsGetCastMemberById_Should_ReturnNotFoundException() {
        final var expectedId = CastMemberID.from("123");
        final var expectedErrorMessage = "CastMember with id 123 was not found";

        final var actualException = assertThrows(
                NotFoundException.class,
                () -> useCase.execute(expectedId.getValue()));

        assertNotNull(actualException);
        assertEquals(expectedErrorMessage, actualException.getMessage());

        verify(castMemberGateway).findById(any());
    }

    @Test
    void Given_AValidId_When_GatewayThrowsException_Should_ReturnException() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();
        final var aMember = CastMember.newMember(expectedName, expectedType);
        final var expectedId = aMember.getId();
        final var expectedErrorMessage = "Gateway error";

        doThrow(new IllegalStateException(expectedErrorMessage)).when(castMemberGateway).findById(expectedId);

        final var exception = assertThrows(
                IllegalStateException.class,
                () -> useCase.execute(expectedId.getValue()));

        assertEquals(expectedErrorMessage, exception.getMessage());

        verify(castMemberGateway).findById(any());
    }

}
