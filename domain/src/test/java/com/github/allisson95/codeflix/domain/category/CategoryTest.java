package com.github.allisson95.codeflix.domain.category;

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

}
