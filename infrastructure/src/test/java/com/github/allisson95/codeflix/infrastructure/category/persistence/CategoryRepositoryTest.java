package com.github.allisson95.codeflix.infrastructure.category.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import com.github.allisson95.codeflix.domain.category.Category;
import com.github.allisson95.codeflix.infrastructure.MySQLGatewayTest;

@MySQLGatewayTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void Given_AnInvalidName_When_CallSaveCategory_Then_ThrowsAnException() {
        final var expectedPropertyName = "name";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.github.allisson95.codeflix.infrastructure.category.persistence.CategoryJpaEntity.name";

        final var aCategory = Category.newCategory("Filme", "A categoria mais assistida", true);

        final var aCategoryEntity = CategoryJpaEntity.from(aCategory);
        aCategoryEntity.setName(null);

        final var exception = assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(aCategoryEntity));

        final var cause = assertInstanceOf(PropertyValueException.class, exception.getCause());

        assertEquals(expectedPropertyName, cause.getPropertyName());

        assertEquals(expectedErrorMessage, cause.getMessage());
    }

    @Test
    void Given_AnInvalidCreatedAt_When_CallSaveCategory_Then_ThrowsAnException() {
        final var expectedPropertyName = "createdAt";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.github.allisson95.codeflix.infrastructure.category.persistence.CategoryJpaEntity.createdAt";

        final var aCategory = Category.newCategory("Filme", "A categoria mais assistida", true);

        final var aCategoryEntity = CategoryJpaEntity.from(aCategory);
        aCategoryEntity.setCreatedAt(null);

        final var exception = assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(aCategoryEntity));

        final var cause = assertInstanceOf(PropertyValueException.class, exception.getCause());

        assertEquals(expectedPropertyName, cause.getPropertyName());

        assertEquals(expectedErrorMessage, cause.getMessage());
    }

    @Test
    void Given_AnInvalidUpdatedAt_When_CallSaveCategory_Then_ThrowsAnException() {
        final var expectedPropertyName = "updatedAt";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.github.allisson95.codeflix.infrastructure.category.persistence.CategoryJpaEntity.updatedAt";

        final var aCategory = Category.newCategory("Filme", "A categoria mais assistida", true);

        final var aCategoryEntity = CategoryJpaEntity.from(aCategory);
        aCategoryEntity.setUpdatedAt(null);

        final var exception = assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(aCategoryEntity));

        final var cause = assertInstanceOf(PropertyValueException.class, exception.getCause());

        assertEquals(expectedPropertyName, cause.getPropertyName());

        assertEquals(expectedErrorMessage, cause.getMessage());
    }

}
