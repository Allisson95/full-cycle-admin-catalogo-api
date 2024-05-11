package com.github.allisson95.codeflix.domain.castmember;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.allisson95.codeflix.domain.UnitTest;
import com.github.allisson95.codeflix.domain.exceptions.NotificationException;

class CastMemberTest extends UnitTest {

    @Test
    void Given_ValidParams_When_CallNewCastMember_Then_InstantiateACastMember() {
        final var expectedName = "Vin Diesel";
        final var expectedType = CastMemberType.ACTOR;

        final var actualMember = CastMember.newMember(expectedName, expectedType);

        assertNotNull(actualMember);
        assertNotNull(actualMember.getId());
        assertEquals(expectedName, actualMember.getName());
        assertEquals(expectedType, actualMember.getType());
        assertNotNull(actualMember.getCreatedAt());
        assertNotNull(actualMember.getUpdatedAt());
        assertEquals(actualMember.getCreatedAt(), actualMember.getUpdatedAt());
    }

    @Test
    void Given_InvalidNullName_When_CallsNewMember_Should_ReceiveANotification() {
        final String expectedName = null;
        final var expectedType = CastMemberType.ACTOR;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var actualException = assertThrows(
                NotificationException.class,
                () -> CastMember.newMember(expectedName, expectedType));

        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void Given_InvalidEmptyName_When_CallsNewMember_Should_ReceiveANotification() {
        final var expectedName = " ";
        final var expectedType = CastMemberType.ACTOR;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        final var actualException = assertThrows(
                NotificationException.class,
                () -> CastMember.newMember(expectedName, expectedType));

        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void Given_InvalidNameWithLengthMoreThan255_When_CallsNewMember_Should_ReceiveANotification() {
        final var expectedName = """
                    A prática cotidiana prova que a complexidade dos estudos efetuados facilita a criação das regras de conduta normativas.
                    Nunca é demais lembrar o peso e o significado destes problemas, uma vez que a hegemonia do ambiente político desafia a
                    capacidade de equalização de todos os recursos funcionais envolvidos.
                """;
        final var expectedType = CastMemberType.ACTOR;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' must be between 3 and 255 characteres";

        final var actualException = assertThrows(
                NotificationException.class,
                () -> CastMember.newMember(expectedName, expectedType));

        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void Given_InvalidType_When_CallsNewMember_Should_ReceiveANotification() {
        final var expectedName = "Vin Diesel";
        final CastMemberType expectedType = null;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'type' should not be null";

        final var actualException = assertThrows(
                NotificationException.class,
                () -> CastMember.newMember(expectedName, expectedType));

        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void Given_AValidMember_When_CallUpdateCastMemberWithValidValues_Then_ReturnACastMemberUpdated() {
        final var expectedName = "Vin Diesel";
        final var expectedType = CastMemberType.ACTOR;

        final var actualMember = CastMember.newMember("vind", CastMemberType.DIRECTOR);

        assertNotNull(actualMember);
        assertNotNull(actualMember.getId());

        final var createdAt = actualMember.getCreatedAt();
        final var updatedAt = actualMember.getUpdatedAt();

        final var updatedMember = actualMember.update(expectedName, expectedType);

        assertNotNull(updatedMember);
        assertEquals(actualMember.getId(), updatedMember.getId());
        assertEquals(expectedName, updatedMember.getName());
        assertEquals(expectedType, updatedMember.getType());
        assertEquals(createdAt, updatedMember.getCreatedAt());
        assertTrue(updatedAt.isBefore(updatedMember.getUpdatedAt()));
    }

    @Test
    void Given_AValidMember_When_CallUpdateCastMemberWithNullName_Should_ReceiveANotification() {
        final String expectedName = null;
        final var expectedType = CastMemberType.ACTOR;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var actualMember = CastMember.newMember("Vin Diesel", expectedType);

        assertNotNull(actualMember);
        assertNotNull(actualMember.getId());

        final var actualException = assertThrows(
                NotificationException.class,
                () -> actualMember.update(expectedName, expectedType));

        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void Given_AValidMember_When_CallUpdateCastMemberWithEmptyName_Should_ReceiveANotification() {
        final var expectedName = " ";
        final var expectedType = CastMemberType.ACTOR;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        final var actualMember = CastMember.newMember("Vin Diesel", expectedType);

        assertNotNull(actualMember);
        assertNotNull(actualMember.getId());

        final var actualException = assertThrows(
                NotificationException.class,
                () -> actualMember.update(expectedName, expectedType));

        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void Given_AValidMember_When_CallUpdateCastMemberWithNameLengthMoreThan255_Should_ReceiveANotification() {
        final var expectedName = """
                    A prática cotidiana prova que a complexidade dos estudos efetuados facilita a criação das regras de conduta normativas.
                    Nunca é demais lembrar o peso e o significado destes problemas, uma vez que a hegemonia do ambiente político desafia a
                    capacidade de equalização de todos os recursos funcionais envolvidos.
                """;
        final var expectedType = CastMemberType.ACTOR;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' must be between 3 and 255 characteres";

        final var actualMember = CastMember.newMember("Vin Diesel", expectedType);

        assertNotNull(actualMember);
        assertNotNull(actualMember.getId());

        final var actualException = assertThrows(
                NotificationException.class,
                () -> actualMember.update(expectedName, expectedType));

        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void Given_AValidMember_When_CallUpdateCastMemberWithInvalidType_Should_ReceiveANotification() {
        final var expectedName = "Vin Diesel";
        final CastMemberType expectedType = null;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'type' should not be null";

        final var actualMember = CastMember.newMember(expectedName, CastMemberType.DIRECTOR);

        assertNotNull(actualMember);
        assertNotNull(actualMember.getId());

        final var actualException = assertThrows(
                NotificationException.class,
                () -> actualMember.update(expectedName, expectedType));

        assertNotNull(actualException);
        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

}
