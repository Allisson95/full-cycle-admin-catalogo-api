package com.github.allisson95.codeflix.infrastructure.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.allisson95.codeflix.domain.category.Category;
import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.infrastructure.MySQLGatewayTest;
import com.github.allisson95.codeflix.infrastructure.category.persistence.CategoryJpaEntity;
import com.github.allisson95.codeflix.infrastructure.category.persistence.CategoryRepository;

@MySQLGatewayTest
class CategoryMySQLGatewayTest {

    @Autowired
    private CategoryMySQLGateway categoryGateway;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void Given_AValidCategory_When_CallCreate_Then_ReturnANewCategory() {
        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        assertEquals(0, categoryRepository.count());

        final var createdCategory = categoryGateway.create(aCategory);

        assertEquals(1, categoryRepository.count());

        assertEquals(aCategory.getId(), createdCategory.getId());
        assertEquals(expectedName, createdCategory.getName());
        assertEquals(expectedDescription, createdCategory.getDescription());
        assertEquals(expectedIsActive, createdCategory.isActive());
        assertEquals(aCategory.getCreatedAt(), createdCategory.getCreatedAt());
        assertEquals(aCategory.getUpdatedAt(), createdCategory.getUpdatedAt());
        assertEquals(aCategory.getDeletedAt(), createdCategory.getDeletedAt());
        assertNull(createdCategory.getDeletedAt());

        final var createdCategoryJpaEntity = categoryRepository.findById(aCategory.getId().getValue()).get();

        assertEquals(aCategory.getId().getValue(), createdCategoryJpaEntity.getId());
        assertEquals(expectedName, createdCategoryJpaEntity.getName());
        assertEquals(expectedDescription, createdCategoryJpaEntity.getDescription());
        assertEquals(expectedIsActive, createdCategoryJpaEntity.isActive());
        assertEquals(aCategory.getCreatedAt(), createdCategoryJpaEntity.getCreatedAt());
        assertEquals(aCategory.getUpdatedAt(), createdCategoryJpaEntity.getUpdatedAt());
        assertEquals(aCategory.getDeletedAt(), createdCategoryJpaEntity.getDeletedAt());
        assertNull(createdCategoryJpaEntity.getDeletedAt());
    }

    @Test
    void Given_AValidCategory_When_CallUpdate_Then_ReturnUpdatedCategory() {
        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory("Film", null, false);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        assertEquals(1, categoryRepository.count());

        final var invalidCategoryJpaEntity = categoryRepository.findById(aCategory.getId().getValue()).get();

        assertEquals(aCategory.getId().getValue(), invalidCategoryJpaEntity.getId());
        assertEquals(aCategory.getName(), invalidCategoryJpaEntity.getName());
        assertEquals(aCategory.getDescription(), invalidCategoryJpaEntity.getDescription());
        assertEquals(aCategory.isActive(), invalidCategoryJpaEntity.isActive());
        assertEquals(aCategory.getCreatedAt(), invalidCategoryJpaEntity.getCreatedAt());
        assertEquals(aCategory.getUpdatedAt(), invalidCategoryJpaEntity.getUpdatedAt());
        assertEquals(aCategory.getDeletedAt(), invalidCategoryJpaEntity.getDeletedAt());
        assertNotNull(invalidCategoryJpaEntity.getDeletedAt());

        final var anUpdatedCategory = Category.with(aCategory)
                .update(expectedName, expectedDescription, expectedIsActive);

        final var updatedCategory = categoryGateway.update(anUpdatedCategory);

        assertEquals(1, categoryRepository.count());

        assertEquals(aCategory.getId(), updatedCategory.getId());
        assertEquals(expectedName, updatedCategory.getName());
        assertEquals(expectedDescription, updatedCategory.getDescription());
        assertEquals(expectedIsActive, updatedCategory.isActive());
        assertEquals(aCategory.getCreatedAt(), updatedCategory.getCreatedAt());
        assertTrue(aCategory.getUpdatedAt().isBefore(updatedCategory.getUpdatedAt()));
        assertNull(updatedCategory.getDeletedAt());

        final var createdCategoryJpaEntity = categoryRepository.findById(aCategory.getId().getValue()).get();

        assertEquals(aCategory.getId().getValue(), createdCategoryJpaEntity.getId());
        assertEquals(expectedName, createdCategoryJpaEntity.getName());
        assertEquals(expectedDescription, createdCategoryJpaEntity.getDescription());
        assertEquals(expectedIsActive, createdCategoryJpaEntity.isActive());
        assertEquals(aCategory.getCreatedAt(), createdCategoryJpaEntity.getCreatedAt());
        assertTrue(aCategory.getUpdatedAt().isBefore(createdCategoryJpaEntity.getUpdatedAt()));
        assertNull(createdCategoryJpaEntity.getDeletedAt());
    }

    @Test
    void Given_APrePersistedCategoryAndValidCategoryId_When_TryToDeleteIt_Then_DeleteCategory() {
        final var aCategory = Category.newCategory("Filme", null, true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        assertEquals(1, categoryRepository.count());

        categoryGateway.deleteById(aCategory.getId());

        assertEquals(0, categoryRepository.count());
    }

    @Test
    void Given_AnIvalidCategoryId_When_TryToDeleteIt_Then_DeleteCategory() {
        assertEquals(0, categoryRepository.count());

        categoryGateway.deleteById(CategoryID.from("invalid"));

        assertEquals(0, categoryRepository.count());
    }

    @Test
    void Given_APrePersistedCategoryAndValidCategoryId_When_CallFindById_Then_ReturnCategory() {
        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        assertEquals(1, categoryRepository.count());

        final var actualCategory = categoryGateway.findById(aCategory.getId()).get();

        assertEquals(1, categoryRepository.count());

        assertEquals(aCategory.getId(), actualCategory.getId());
        assertEquals(expectedName, actualCategory.getName());
        assertEquals(expectedDescription, actualCategory.getDescription());
        assertEquals(expectedIsActive, actualCategory.isActive());
        assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
        assertEquals(aCategory.getUpdatedAt(), actualCategory.getUpdatedAt());
        assertEquals(aCategory.getDeletedAt(), actualCategory.getDeletedAt());
    }

    @Test
    void Given_ACategoryIdNotStored_When_CallFindById_Then_ReturnEmpty() {
        assertEquals(0, categoryRepository.count());

        final var actualCategory = categoryGateway.findById(CategoryID.from("empty"));

        assertEquals(0, categoryRepository.count());

        assertTrue(actualCategory.isEmpty());
    }

}
