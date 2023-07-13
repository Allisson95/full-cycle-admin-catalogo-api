package com.github.allisson95.codeflix.application.castmember.delete;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.github.allisson95.codeflix.IntegrationTest;
import com.github.allisson95.codeflix.Fixture;
import com.github.allisson95.codeflix.domain.castmember.CastMember;
import com.github.allisson95.codeflix.domain.castmember.CastMemberGateway;
import com.github.allisson95.codeflix.domain.castmember.CastMemberID;
import com.github.allisson95.codeflix.infrastructure.castmember.persistence.CastMemberJpaEntity;
import com.github.allisson95.codeflix.infrastructure.castmember.persistence.CastMemberRepository;

@IntegrationTest
class DeleteCastMemberUseCaseIT {

    @Autowired
    private CastMemberRepository castMemberRepository;

    @SpyBean
    private CastMemberGateway castMemberGateway;

    @Autowired
    private DeleteCastMemberUseCase useCase;

    @Test
    void Given_AValidId_When_CallsDeleteCastMember_Should_Be_Ok() {
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMember.type());
        this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));

        final var expectedId = aMember.getId();

        assertEquals(1, this.castMemberRepository.count());

        assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        assertEquals(0, this.castMemberRepository.count());

        verify(castMemberGateway).deleteById(any());
    }

    @Test
    void Given_AInvalidId_When_CallsDeleteCastMember_Should_Be_Ok() {
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMember.type());
        this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));

        final var expectedId = CastMemberID.from("invalid");

        assertEquals(1, this.castMemberRepository.count());

        assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        assertEquals(1, this.castMemberRepository.count());

        verify(castMemberGateway).deleteById(any());
    }

    @Test
    void Given_AValidId_When_CallsDeleteCastMemberAndGatewayThrowsException_Should_ReturnException() {
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMember.type());
        this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));
        final var expectedId = aMember.getId();
        final var expectedErrorMessage = "Gateway error";

        assertEquals(1, this.castMemberRepository.count());

        doThrow(new IllegalStateException(expectedErrorMessage)).when(castMemberGateway).deleteById(any());

        assertThrows(IllegalStateException.class, () -> useCase.execute(expectedId.getValue()));

        assertEquals(1, this.castMemberRepository.count());

        verify(castMemberGateway).deleteById(any());
    }

}
