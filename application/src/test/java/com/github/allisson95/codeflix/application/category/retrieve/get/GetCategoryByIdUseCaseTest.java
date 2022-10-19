package com.github.allisson95.codeflix.application.category.retrieve.get;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.allisson95.codeflix.domain.category.Category;
import com.github.allisson95.codeflix.domain.category.CategoryGateway;
import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.domain.exceptions.DomainException;

@ExtendWith(MockitoExtension.class)
class GetCategoryByIdUseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @InjectMocks
    private DefaultGetCategoryByIdUseCase useCase;

    @BeforeEach
    void cleanUp() {
        reset(categoryGateway);
    }

    @Test
    void Given_AValidId_When_CallsGetCategoryById_Should_BeOk() {
        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final var expectedId = aCategory.getId();

        when(categoryGateway.findById(expectedId)).thenReturn(Optional.of(Category.with(aCategory)));

        final var actualCategory = useCase.execute(expectedId.getValue());

        assertEquals(expectedId, actualCategory.id());
        assertEquals(expectedName, actualCategory.name());
        assertEquals(expectedDescription, actualCategory.description());
        assertEquals(expectedIsActive, actualCategory.isActive());
        assertEquals(aCategory.getCreatedAt(), actualCategory.createdAt());
        assertEquals(aCategory.getUpdatedAt(), actualCategory.updatedAt());
        assertEquals(aCategory.getDeletedAt(), actualCategory.deletedAt());

        verify(categoryGateway, times(1)).findById(expectedId);
    }

    @Test
    void Given_AInvalidId_When_CallsGetCategoryById_Should_ReturnNotFound() {
        final var expectedId = CategoryID.from("123");
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "Category with id 123 was not found";

        when(categoryGateway.findById(expectedId)).thenReturn(Optional.empty());

        final var exception = assertThrows(DomainException.class, () -> useCase.execute(expectedId.getValue()));

        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());

        verify(categoryGateway, times(1)).findById(expectedId);
    }

    @Test
    void Given_AValidId_When_GatewayThrowsException_Should_ReturnException() {
        final var aCategory = Category.newCategory("Filme", "A categoria mais assistida", true);
        final var expectedId = aCategory.getId();
        final var expectedErrorMessage = "Gateway error";

        when(categoryGateway.findById(expectedId)).thenThrow(new IllegalStateException(expectedErrorMessage));

        final var exception = assertThrows(IllegalStateException.class, () -> useCase.execute(expectedId.getValue()));

        assertEquals(expectedErrorMessage, exception.getMessage());

        verify(categoryGateway, times(1)).findById(expectedId);
    }

}
