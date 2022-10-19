package com.github.allisson95.codeflix.application.category.delete;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.allisson95.codeflix.domain.category.Category;
import com.github.allisson95.codeflix.domain.category.CategoryGateway;
import com.github.allisson95.codeflix.domain.category.CategoryID;

@ExtendWith(MockitoExtension.class)
class DeleteCategoryUseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @InjectMocks
    private DefaultDeleteCategoryUseCase useCase;

    @BeforeEach
    void cleanUp() {
        reset(categoryGateway);
    }

    @Test
    void Given_AValidId_When_CallsDeleteCategory_Should_BeOk() {
        final var aCategory = Category.newCategory("Filme", "A categoria mais assistida", true);
        final var expectedId = aCategory.getId();

        doNothing().when(categoryGateway).deleteById(expectedId);

        assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        verify(categoryGateway, times(1)).deleteById(expectedId);
    }

    @Test
    void Given_AInvalidId_When_CallsDeleteCategory_Should_BeOk() {
        final var expectedId = CategoryID.from("123");

        doNothing().when(categoryGateway).deleteById(expectedId);

        assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        verify(categoryGateway, times(1)).deleteById(expectedId);
    }

    @Test
    void Given_AValidId_When_GatewayThrowsException_Should_ReturnException() {
        final var aCategory = Category.newCategory("Filme", "A categoria mais assistida", true);
        final var expectedId = aCategory.getId();
        final var expectedErrorMessage = "Gateway error";

        doThrow(new IllegalStateException(expectedErrorMessage)).when(categoryGateway).deleteById(expectedId);

        assertThrows(IllegalStateException.class, () -> useCase.execute(expectedId.getValue()));

        verify(categoryGateway, times(1)).deleteById(expectedId);
    }

}
