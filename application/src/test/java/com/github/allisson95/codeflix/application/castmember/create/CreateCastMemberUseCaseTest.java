package com.github.allisson95.codeflix.application.castmember.create;

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

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.github.allisson95.codeflix.application.UseCaseTest;
import com.github.allisson95.codeflix.domain.Fixture;
import com.github.allisson95.codeflix.domain.castmember.CastMemberGateway;
import com.github.allisson95.codeflix.domain.castmember.CastMemberType;
import com.github.allisson95.codeflix.domain.exceptions.NotificationException;

class CreateCastMemberUseCaseTest extends UseCaseTest {

    @Mock
    private CastMemberGateway castMemberGateway;

    @InjectMocks
    private DefaultCreateCastMemberUseCase useCase;

    @Override
    protected List<Object> getMocksForClean() {
        return List.of(castMemberGateway);
    }

    @Test
    void Given_AValidCommand_When_CallCreateCastMember_Should_ReturnCastMemberId() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();

        final var aCommand = CreateCastMemberCommand.with(expectedName, expectedType);

        when(castMemberGateway.create(any())).thenAnswer(returnsFirstArg());

        final var createdMember = useCase.execute(aCommand);

        assertNotNull(createdMember);
        assertNotNull(createdMember.id());

        verify(castMemberGateway, times(1))
                .create(argThat(
                        aMember -> Objects.equals(expectedName, aMember.getName())
                                && Objects.equals(expectedType, aMember.getType())
                                && Objects.nonNull(aMember.getId())
                                && Objects.nonNull(aMember.getCreatedAt())
                                && Objects.nonNull(aMember.getUpdatedAt())));
    }

    @Test
    void Given_AInvalidNullName_When_CallsCreateCastMember_Should_ReceiveNotificationException() {
        final String expectedName = null;
        final var expectedType = Fixture.CastMembers.type();
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
        final var expectedType = Fixture.CastMembers.type();
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
        final var expectedType = Fixture.CastMembers.type();
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
