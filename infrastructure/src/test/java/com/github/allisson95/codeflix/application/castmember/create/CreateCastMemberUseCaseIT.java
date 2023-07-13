package com.github.allisson95.codeflix.application.castmember.create;

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
import com.github.allisson95.codeflix.domain.castmember.CastMemberGateway;
import com.github.allisson95.codeflix.domain.castmember.CastMemberType;
import com.github.allisson95.codeflix.domain.exceptions.NotificationException;
import com.github.allisson95.codeflix.infrastructure.castmember.persistence.CastMemberRepository;

@IntegrationTest
class CreateCastMemberUseCaseIT {

    @Autowired
    private CastMemberRepository castMemberRepository;

    @SpyBean
    private CastMemberGateway castMemberGateway;

    @Autowired
    private CreateCastMemberUseCase useCase;

    @Test
    void Given_AValidCommand_When_CallCreateCastMember_Should_ReturnCastMemberId() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();

        final var aCommand = CreateCastMemberCommand.with(expectedName, expectedType);

        final var createdMember = useCase.execute(aCommand);

        assertNotNull(createdMember);
        assertNotNull(createdMember.id());

        final var persistedMember = this.castMemberRepository.findById(createdMember.id()).get();

        assertEquals(expectedName, persistedMember.getName());
        assertEquals(expectedType, persistedMember.getType());
        assertNotNull(persistedMember.getCreatedAt());
        assertNotNull(persistedMember.getUpdatedAt());
        assertEquals(persistedMember.getCreatedAt(), persistedMember.getUpdatedAt());

        verify(castMemberGateway).create(any());
    }

    @Test
    void Given_AInvalidNullName_When_CallsCreateCastMember_Should_ReceiveNotificationException() {
        final String expectedName = null;
        final var expectedType = Fixture.CastMember.type();
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var aCommand = CreateCastMemberCommand.with(expectedName, expectedType);

        final var actualException = assertThrows(
                NotificationException.class,
                () -> useCase.execute(aCommand));

        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(castMemberGateway, times(0)).create(any());
    }

    @Test
    void Given_AInvalidEmptyName_WhenCallsCreateCastMember_Should_ReceiveNotificationException() {
        final var expectedName = " ";
        final var expectedType = Fixture.CastMember.type();
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        final var aCommand = CreateCastMemberCommand.with(expectedName, expectedType);

        final var actualException = assertThrows(
                NotificationException.class,
                () -> useCase.execute(aCommand));

        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(castMemberGateway, times(0)).create(any());
    }

    @Test
    void Given_AInvalidNameWithLengthMoreThan255_WhenCallsCreateCastMember_Should_ReceiveNotificationException() {
        final var expectedName = """
                    A prática cotidiana prova que a complexidade dos estudos efetuados facilita a criação das regras de conduta normativas.
                    Nunca é demais lembrar o peso e o significado destes problemas, uma vez que a hegemonia do ambiente político desafia a
                    capacidade de equalização de todos os recursos funcionais envolvidos.
                """;
        final var expectedType = Fixture.CastMember.type();
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' must be between 3 and 255 characteres";

        final var aCommand = CreateCastMemberCommand.with(expectedName, expectedType);

        final var actualException = assertThrows(
                NotificationException.class,
                () -> useCase.execute(aCommand));

        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(castMemberGateway, times(0)).create(any());
    }

    @Test
    void Given_AInvalidType_When_CallsCreateCastMember_Should_ReceiveNotificationException() {
        final var expectedName = Fixture.name();
        final CastMemberType expectedType = null;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'type' should not be null";

        final var aCommand = CreateCastMemberCommand.with(expectedName, expectedType);

        final var actualException = assertThrows(
                NotificationException.class,
                () -> useCase.execute(aCommand));

        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(castMemberGateway, times(0)).create(any());
    }

}
