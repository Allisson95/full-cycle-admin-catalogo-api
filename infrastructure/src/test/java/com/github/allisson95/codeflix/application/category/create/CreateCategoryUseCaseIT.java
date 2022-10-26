package com.github.allisson95.codeflix.application.category.create;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.github.allisson95.codeflix.IntegrationTest;
import com.github.allisson95.codeflix.domain.category.CategoryGateway;
import com.github.allisson95.codeflix.infrastructure.category.persistence.CategoryRepository;

@IntegrationTest
class CreateCategoryUseCaseIT {

    @Autowired
    private CreateCategoryUseCase useCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @SpyBean
    private CategoryGateway categoryGateway;

    @Test
    void Given_AValidCommand_When_CallCreateCategory_Then_ReturnCategoryId() {
        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        assertEquals(0, categoryRepository.count());

        final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        final var createdCategory = useCase.execute(aCommand).get();

        assertEquals(1, categoryRepository.count());

        assertNotNull(createdCategory);
        assertNotNull(createdCategory.id());

        final var createdCategoryJpaEntity = categoryRepository.findById(createdCategory.id().getValue()).get();

        assertEquals(createdCategory.id().getValue(), createdCategoryJpaEntity.getId());
        assertEquals(expectedName, createdCategoryJpaEntity.getName());
        assertEquals(expectedDescription, createdCategoryJpaEntity.getDescription());
        assertEquals(expectedIsActive, createdCategoryJpaEntity.isActive());
        assertNotNull(createdCategoryJpaEntity.getCreatedAt());
        assertNotNull(createdCategoryJpaEntity.getUpdatedAt());
        assertNull(createdCategoryJpaEntity.getDeletedAt());
    }

    @Test
    void Given_AnInvalidName_When_CallCreateCategory_Then_ReturnDomainException() {
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        assertEquals(0, categoryRepository.count());

        final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        final var notification = useCase.execute(aCommand).getLeft();

        assertEquals(0, categoryRepository.count());

        assertEquals(expectedErrorCount, notification.getErrors().size());
        assertEquals(expectedErrorMessage, notification.firstError().message());

        verify(categoryGateway, times(0)).create(any());
    }

    @Test
    void Given_AValidCommandWithInactiveCategory_When_CallCreateCategory_Then_ReturnInactiveCategoryId() {
        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        assertEquals(0, categoryRepository.count());

        final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        final var createdCategory = useCase.execute(aCommand).get();

        assertEquals(1, categoryRepository.count());

        assertNotNull(createdCategory);
        assertNotNull(createdCategory.id());

        final var createdCategoryJpaEntity = categoryRepository.findById(createdCategory.id().getValue()).get();

        assertEquals(createdCategory.id().getValue(), createdCategoryJpaEntity.getId());
        assertEquals(expectedName, createdCategoryJpaEntity.getName());
        assertEquals(expectedDescription, createdCategoryJpaEntity.getDescription());
        assertEquals(expectedIsActive, createdCategoryJpaEntity.isActive());
        assertNotNull(createdCategoryJpaEntity.getCreatedAt());
        assertNotNull(createdCategoryJpaEntity.getUpdatedAt());
        assertNotNull(createdCategoryJpaEntity.getDeletedAt());
    }

    @Test
    void Given_AValidCommand_When_GatewayThrowsRandomException_Then_ReturnAException() {
        final String expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "Gateway error";

        assertEquals(0, categoryRepository.count());

        final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        doThrow(new IllegalStateException(expectedErrorMessage)).when(categoryGateway).create(any());

        final var notification = useCase.execute(aCommand).getLeft();

        assertEquals(0, categoryRepository.count());

        assertEquals(expectedErrorCount, notification.getErrors().size());
        assertEquals(expectedErrorMessage, notification.firstError().message());

        verify(categoryGateway, times(1)).create(any());
    }

}
