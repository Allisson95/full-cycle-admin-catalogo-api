package com.github.allisson95.codeflix.domain.genre;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.domain.exceptions.NotificationException;

class GenreTest {

    @Test
    void Given_AValidParams_When_CallNewGenre_Should_InstantiateAGenre() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategoriesSize = 0;

        final var actualGenre = Genre.newGenre(expectedName, expectedIsActive);

        assertNotNull(actualGenre);
        assertNotNull(actualGenre.getId());
        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertEquals(expectedCategoriesSize, actualGenre.getCategories().size());
        assertNotNull(actualGenre.getCreatedAt());
        assertNotNull(actualGenre.getUpdatedAt());
        assertNull(actualGenre.getDeletedAt());
    }

    @Test
    void Given_InvalidNullName_When_CallNewGenreAndValidate_Should_ReceiveAnError() {
        final String expectedName = null;
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var actualException = assertThrows(
                NotificationException.class,
                () -> Genre.newGenre(expectedName, expectedIsActive));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void Given_InvalidEmptyName_When_CallNewGenreAndValidate_Should_ReceiveAnError() {
        final var expectedName = "";
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        final var actualException = assertThrows(
                NotificationException.class,
                () -> Genre.newGenre(expectedName, expectedIsActive));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void Given_InvalidNameWithLengthGreaterThan255_When_CallNewGenreAndValidate_Should_ReceiveAnError() {
        final var expectedName = """
                    A prática cotidiana prova que a complexidade dos estudos efetuados facilita a criação das regras de conduta normativas.
                    Nunca é demais lembrar o peso e o significado destes problemas, uma vez que a hegemonia do ambiente político desafia a
                    capacidade de equalização de todos os recursos funcionais envolvidos.
                """;
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' must be between 1 and 255 characteres";

        final var actualException = assertThrows(
                NotificationException.class,
                () -> Genre.newGenre(expectedName, expectedIsActive));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void Given_AnActiveGenre_When_CallsDeactivate_Should_ReturnOK() {
        final var expectedName = "Ação";
        final var expectedIsActive = false;
        final var expectedCategoriesSize = 0;

        final var actualGenre = Genre.newGenre(expectedName, true);

        assertNotNull(actualGenre);
        assertTrue(actualGenre.isActive());
        assertNull(actualGenre.getDeletedAt());

        final var createdAt = actualGenre.getCreatedAt();
        final var updatedAt = actualGenre.getUpdatedAt();

        actualGenre.deactivate();

        assertNotNull(actualGenre.getId());
        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertEquals(expectedCategoriesSize, actualGenre.getCategories().size());
        assertEquals(createdAt, actualGenre.getCreatedAt());
        assertTrue(updatedAt.isBefore(actualGenre.getUpdatedAt()));
        assertNotNull(actualGenre.getDeletedAt());
    }

    @Test
    void Given_AnInactiveGenre_When_CallsActivate_Should_ReturnOK() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategoriesSize = 0;

        final var actualGenre = Genre.newGenre(expectedName, false);

        assertNotNull(actualGenre);
        assertFalse(actualGenre.isActive());
        assertNotNull(actualGenre.getDeletedAt());

        final var createdAt = actualGenre.getCreatedAt();
        final var updatedAt = actualGenre.getUpdatedAt();

        actualGenre.activate();

        assertNotNull(actualGenre.getId());
        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertEquals(expectedCategoriesSize, actualGenre.getCategories().size());
        assertEquals(createdAt, actualGenre.getCreatedAt());
        assertTrue(updatedAt.isBefore(actualGenre.getUpdatedAt()));
        assertNull(actualGenre.getDeletedAt());
    }

    @Test
    void Given_AValidInactiveGenre_When_CallUpdateWithActivate_Should_ReceiveGenreUpdated() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(CategoryID.from("123"));

        final var actualGenre = Genre.newGenre("acao", false);

        assertNotNull(actualGenre);
        assertFalse(actualGenre.isActive());
        assertNotNull(actualGenre.getDeletedAt());

        final var createdAt = actualGenre.getCreatedAt();
        final var updatedAt = actualGenre.getUpdatedAt();

        actualGenre.update(expectedName, expectedIsActive, expectedCategories);

        assertNotNull(actualGenre.getId());
        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertEquals(expectedCategories, actualGenre.getCategories());
        assertEquals(createdAt, actualGenre.getCreatedAt());
        assertTrue(updatedAt.isBefore(actualGenre.getUpdatedAt()));
        assertNull(actualGenre.getDeletedAt());
    }

    @Test
    void Given_AValidActiveGenre_When_CallUpdateWithInactivate_Should_ReceiveGenreUpdated() {
        final var expectedName = "Ação";
        final var expectedIsActive = false;
        final var expectedCategories = List.of(CategoryID.from("123"));

        final var actualGenre = Genre.newGenre("acao", true);

        assertNotNull(actualGenre);
        assertTrue(actualGenre.isActive());
        assertNull(actualGenre.getDeletedAt());

        final var createdAt = actualGenre.getCreatedAt();
        final var updatedAt = actualGenre.getUpdatedAt();

        actualGenre.update(expectedName, expectedIsActive, expectedCategories);

        assertNotNull(actualGenre.getId());
        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertEquals(expectedCategories, actualGenre.getCategories());
        assertEquals(createdAt, actualGenre.getCreatedAt());
        assertTrue(updatedAt.isBefore(actualGenre.getUpdatedAt()));
        assertNotNull(actualGenre.getDeletedAt());
    }

    @Test
    void Given_AValidGenre_When_CallUpdateWithNullName_Should_ReceiveAnError() {
        final String expectedName = null;
        final var expectedIsActive = false;
        final var expectedCategories = List.of(CategoryID.from("123"));
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var actualGenre = Genre.newGenre("acao", true);

        assertNotNull(actualGenre);
        assertTrue(actualGenre.isActive());
        assertNull(actualGenre.getDeletedAt());

        final var actualException = assertThrows(
                NotificationException.class,
                () -> actualGenre.update(expectedName, expectedIsActive, expectedCategories));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void Given_AValidGenre_When_CallUpdateWithEmptyName_Should_ReceiveAnError() {
        final var expectedName = " ";
        final var expectedIsActive = false;
        final var expectedCategories = List.of(CategoryID.from("123"));
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        final var actualGenre = Genre.newGenre("acao", true);

        assertNotNull(actualGenre);
        assertTrue(actualGenre.isActive());
        assertNull(actualGenre.getDeletedAt());

        final var actualException = assertThrows(
                NotificationException.class,
                () -> actualGenre.update(expectedName, expectedIsActive, expectedCategories));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void Given_AValidGenre_When_CallUpdateWithInvalidNameWithLengthGreaterThan255_Should_ReceiveAnError() {
        final var expectedName = """
                    A prática cotidiana prova que a complexidade dos estudos efetuados facilita a criação das regras de conduta normativas.
                    Nunca é demais lembrar o peso e o significado destes problemas, uma vez que a hegemonia do ambiente político desafia a
                    capacidade de equalização de todos os recursos funcionais envolvidos.
                """;
        final var expectedIsActive = false;
        final var expectedCategories = List.of(CategoryID.from("123"));
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' must be between 1 and 255 characteres";

        final var actualGenre = Genre.newGenre("acao", true);

        assertNotNull(actualGenre);
        assertTrue(actualGenre.isActive());
        assertNull(actualGenre.getDeletedAt());

        final var actualException = assertThrows(
                NotificationException.class,
                () -> actualGenre.update(expectedName, expectedIsActive, expectedCategories));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

}
