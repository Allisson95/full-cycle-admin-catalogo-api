package com.github.allisson95.codeflix.domain.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

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

}
