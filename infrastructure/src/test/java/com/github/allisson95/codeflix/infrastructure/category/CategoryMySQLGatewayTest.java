package com.github.allisson95.codeflix.infrastructure.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.allisson95.codeflix.domain.category.Category;
import com.github.allisson95.codeflix.infrastructure.MySQLGatewayTest;
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

}
