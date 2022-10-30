package com.github.allisson95.codeflix.application.category.update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.github.allisson95.codeflix.IntegrationTest;
import com.github.allisson95.codeflix.domain.category.Category;
import com.github.allisson95.codeflix.domain.category.CategoryGateway;
import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.domain.exceptions.DomainException;
import com.github.allisson95.codeflix.infrastructure.category.persistence.CategoryJpaEntity;
import com.github.allisson95.codeflix.infrastructure.category.persistence.CategoryRepository;

@IntegrationTest
class UpdateCategoryUseCaseIT {

    @Autowired
    private UpdateCategoryUseCase useCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @SpyBean
    private CategoryGateway categoryGateway;

    @Test
    void Given_AValidCommand_When_CallUpdateCategory_Then_ReturnCategoryId() {
        final var aCategory = Category.newCategory("Film", null, false);

        save(aCategory);

        final var expectedId = aCategory.getId();
        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCommand = UpdateCategoryCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedIsActive);

        assertEquals(1, categoryRepository.count());

        final var updatedCategory = useCase.execute(aCommand).get();

        assertEquals(1, categoryRepository.count());

        assertNotNull(updatedCategory);
        assertNotNull(updatedCategory.id());

        final var aCategoryJpaEntity = categoryRepository.findById(expectedId.getValue()).get();

        assertEquals(aCategory.getId().getValue(), aCategoryJpaEntity.getId());
        assertEquals(expectedName, aCategoryJpaEntity.getName());
        assertEquals(expectedDescription, aCategoryJpaEntity.getDescription());
        assertEquals(expectedIsActive, aCategoryJpaEntity.isActive());
        assertEquals(aCategory.getCreatedAt(), aCategoryJpaEntity.getCreatedAt());
        assertTrue(aCategory.getUpdatedAt().isBefore(aCategoryJpaEntity.getUpdatedAt()));
        assertNull(aCategoryJpaEntity.getDeletedAt());
    }

    @Test
    void Given_AnInvalidName_When_CallUpdateCategory_Then_ReturnDomainException() {
        final var aCategory = Category.newCategory("Filme", "A categoria mais assistida", false);

        save(aCategory);

        final var expectedId = aCategory.getId();
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var aCommand = UpdateCategoryCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedIsActive);

        assertEquals(1, categoryRepository.count());

        final var notification = useCase.execute(aCommand).getLeft();

        assertEquals(1, categoryRepository.count());

        assertEquals(expectedErrorCount, notification.getErrors().size());
        assertEquals(expectedErrorMessage, notification.firstError().message());

        verify(categoryGateway, times(0)).update(any());

        final var aCategoryJpaEntity = categoryRepository.findById(expectedId.getValue()).get();

        assertEquals(aCategory.getId().getValue(), aCategoryJpaEntity.getId());
        assertEquals(aCategory.getName(), aCategoryJpaEntity.getName());
        assertEquals(aCategory.getDescription(), aCategoryJpaEntity.getDescription());
        assertEquals(aCategory.isActive(), aCategoryJpaEntity.isActive());
        assertEquals(aCategory.getCreatedAt(), aCategoryJpaEntity.getCreatedAt());
        assertEquals(aCategory.getUpdatedAt(), aCategoryJpaEntity.getUpdatedAt());
        assertEquals(aCategory.getDeletedAt(), aCategoryJpaEntity.getDeletedAt());
    }

    @Test
    void Given_AValidInactivateCommand_When_CallUpdateCategory_Then_ReturnInactiveCategoryId() {
        final var aCategory = Category.newCategory("Filme", "A categoria mais assistida", true);

        save(aCategory);

        final var expectedId = aCategory.getId();
        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var aCommand = UpdateCategoryCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedIsActive);

        assertTrue(aCategory.isActive());
        assertNull(aCategory.getDeletedAt());

        assertEquals(1, categoryRepository.count());

        final var updatedCategory = useCase.execute(aCommand).get();

        assertEquals(1, categoryRepository.count());

        assertNotNull(updatedCategory);
        assertNotNull(updatedCategory.id());

        final var aCategoryJpaEntity = categoryRepository.findById(expectedId.getValue()).get();

        assertEquals(aCategory.getId().getValue(), aCategoryJpaEntity.getId());
        assertEquals(expectedName, aCategoryJpaEntity.getName());
        assertEquals(expectedDescription, aCategoryJpaEntity.getDescription());
        assertEquals(expectedIsActive, aCategoryJpaEntity.isActive());
        assertEquals(aCategory.getCreatedAt(), aCategoryJpaEntity.getCreatedAt());
        assertTrue(aCategory.getUpdatedAt().isBefore(aCategoryJpaEntity.getUpdatedAt()));
        assertNotNull(aCategoryJpaEntity.getDeletedAt());
    }

    @Test
    void Given_AValidCommand_When_GatewayThrowsRandomException_Then_ReturnAException() {
        final var aCategory = Category.newCategory("Filme", null, true);

        save(aCategory);

        final var expectedId = aCategory.getId();
        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "Gateway error";

        final var aCommand = UpdateCategoryCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedIsActive);

        doThrow(new IllegalStateException(expectedErrorMessage)).when(categoryGateway).update(any());

        assertEquals(1, categoryRepository.count());

        final var notification = useCase.execute(aCommand).getLeft();

        assertEquals(1, categoryRepository.count());

        assertEquals(expectedErrorCount, notification.getErrors().size());
        assertEquals(expectedErrorMessage, notification.firstError().message());

        verify(categoryGateway, times(1)).findById(expectedId);
        verify(categoryGateway, times(1)).update(any());

        final var aCategoryJpaEntity = categoryRepository.findById(expectedId.getValue()).get();

        assertEquals(aCategory.getId().getValue(), aCategoryJpaEntity.getId());
        assertEquals(aCategory.getName(), aCategoryJpaEntity.getName());
        assertEquals(aCategory.getDescription(), aCategoryJpaEntity.getDescription());
        assertEquals(aCategory.isActive(), aCategoryJpaEntity.isActive());
        assertEquals(aCategory.getCreatedAt(), aCategoryJpaEntity.getCreatedAt());
        assertEquals(aCategory.getUpdatedAt(), aCategoryJpaEntity.getUpdatedAt());
        assertEquals(aCategory.getDeletedAt(), aCategoryJpaEntity.getDeletedAt());
    }

    @Test
    void Given_ACommandWithInvalidID_When_CallUpdateCategory_Then_ReturnNotFoundException() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;
        final var expectedId = "123";
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "Category with id 123 was not found";

        final var aCommand = UpdateCategoryCommand.with(
                expectedId,
                expectedName,
                expectedDescription,
                expectedIsActive);

        final var actualException = assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(categoryGateway, times(1)).findById(CategoryID.from(expectedId));
        verify(categoryGateway, times(0)).update(any());
    }

    private void save(final Category... aCategory) {
        categoryRepository.saveAllAndFlush(
                Arrays.stream(aCategory)
                        .map(CategoryJpaEntity::from)
                        .toList());
    }

}
