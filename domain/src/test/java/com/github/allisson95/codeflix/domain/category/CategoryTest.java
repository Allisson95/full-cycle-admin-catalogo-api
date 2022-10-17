package com.github.allisson95.codeflix.domain.category;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.github.allisson95.codeflix.domain.exceptions.DomainException;
import com.github.allisson95.codeflix.domain.validation.handler.ThrowsValidationHandler;

class CategoryTest {

    @Test
    void Given_ValidParams_When_CallNewCategory_Then_InstantiateACategory() {
        final var expectedName = "Terror";
        final var expectedDescription = "Contos de terror";
        final var expectedIsActive = true;

        final Category newCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final ThrowsValidationHandler aHandler = new ThrowsValidationHandler();

        assertDoesNotThrow(() -> newCategory.validate(aHandler));
        assertNotNull(newCategory);
        assertNotNull(newCategory.getId());
        assertEquals(expectedName, newCategory.getName());
        assertEquals(expectedDescription, newCategory.getDescription());
        assertEquals(expectedIsActive, newCategory.isActive());
        assertNotNull(newCategory.getCreatedAt());
        assertNotNull(newCategory.getUpdatedAt());
        assertNull(newCategory.getDeletedAt());
    }

    @Test
    void Given_ValidNullDescription_When_CallNewCategory_Then_InstantiateACategory() {
        final var expectedName = "Terror";
        final String expectedDescription = null;
        final var expectedIsActive = true;

        final Category newCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final ThrowsValidationHandler aHandler = new ThrowsValidationHandler();

        assertDoesNotThrow(() -> newCategory.validate(aHandler));
        assertNotNull(newCategory);
        assertNotNull(newCategory.getId());
        assertEquals(expectedName, newCategory.getName());
        assertEquals(expectedDescription, newCategory.getDescription());
        assertEquals(expectedIsActive, newCategory.isActive());
        assertNotNull(newCategory.getCreatedAt());
        assertNotNull(newCategory.getUpdatedAt());
        assertNull(newCategory.getDeletedAt());
    }

    @Test
    void Given_ValidEmptyDescription_When_CallNewCategory_Then_InstantiateACategory() {
        final var expectedName = "Terror";
        final var expectedDescription = " ";
        final var expectedIsActive = true;

        final Category newCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final ThrowsValidationHandler aHandler = new ThrowsValidationHandler();

        assertDoesNotThrow(() -> newCategory.validate(aHandler));
        assertNotNull(newCategory);
        assertNotNull(newCategory.getId());
        assertEquals(expectedName, newCategory.getName());
        assertEquals(expectedDescription, newCategory.getDescription());
        assertEquals(expectedIsActive, newCategory.isActive());
        assertNotNull(newCategory.getCreatedAt());
        assertNotNull(newCategory.getUpdatedAt());
        assertNull(newCategory.getDeletedAt());
    }

    @Test
    void Given_ValidFalseIsActive_When_CallNewCategory_Then_InstantiateACategory() {
        final var expectedName = "Terror";
        final var expectedDescription = "Contos de terror";
        final var expectedIsActive = false;

        final Category newCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final ThrowsValidationHandler aHandler = new ThrowsValidationHandler();

        assertDoesNotThrow(() -> newCategory.validate(aHandler));
        assertNotNull(newCategory);
        assertNotNull(newCategory.getId());
        assertEquals(expectedName, newCategory.getName());
        assertEquals(expectedDescription, newCategory.getDescription());
        assertEquals(expectedIsActive, newCategory.isActive());
        assertNotNull(newCategory.getCreatedAt());
        assertNotNull(newCategory.getUpdatedAt());
        assertNotNull(newCategory.getDeletedAt());
    }

    @Test
    void Given_AnInvalidNullName_When_CallNewCategoryAndValidate_Then_ShouldReceiveError() {
        final String expectedName = null;
        final var expectedDescription = "Contos de terror";
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final Category newCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final ThrowsValidationHandler aHandler = new ThrowsValidationHandler();

        final var exception = assertThrows(DomainException.class, () -> newCategory.validate(aHandler));

        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    void Given_AnInvalidEmptyName_When_CallNewCategoryAndValidate_Then_ShouldReceiveError() {
        final var expectedName = " ";
        final var expectedDescription = "Contos de terror";
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        final Category newCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final ThrowsValidationHandler aHandler = new ThrowsValidationHandler();

        final var exception = assertThrows(DomainException.class, () -> newCategory.validate(aHandler));

        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    void Given_AnInvalidNameLengthLessThan3_When_CallNewCategoryAndValidate_Then_ShouldReceiveError() {
        final var expectedName = "Fi ";
        final var expectedDescription = "Contos de terror";
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' must be between 3 and 255 characteres";

        final Category newCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final ThrowsValidationHandler aHandler = new ThrowsValidationHandler();

        final var exception = assertThrows(DomainException.class, () -> newCategory.validate(aHandler));

        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    void Given_AnInvalidNameLengthMoreThan255_When_CallNewCategoryAndValidate_Then_ShouldReceiveError() {
        final var expectedName = """
                    A prática cotidiana prova que a complexidade dos estudos efetuados facilita a criação das regras de conduta normativas.
                    Nunca é demais lembrar o peso e o significado destes problemas, uma vez que a hegemonia do ambiente político desafia a
                    capacidade de equalização de todos os recursos funcionais envolvidos.
                """;
        final var expectedDescription = "Contos de terror";
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' must be between 3 and 255 characteres";

        final Category newCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final ThrowsValidationHandler aHandler = new ThrowsValidationHandler();

        final var exception = assertThrows(DomainException.class, () -> newCategory.validate(aHandler));

        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

}
