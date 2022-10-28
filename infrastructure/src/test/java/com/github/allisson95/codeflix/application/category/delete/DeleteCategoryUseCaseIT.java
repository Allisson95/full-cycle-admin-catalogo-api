package com.github.allisson95.codeflix.application.category.delete;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import com.github.allisson95.codeflix.infrastructure.category.persistence.CategoryJpaEntity;
import com.github.allisson95.codeflix.infrastructure.category.persistence.CategoryRepository;

@IntegrationTest
class DeleteCategoryUseCaseIT {

    @Autowired
    private DeleteCategoryUseCase useCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @SpyBean
    private CategoryGateway categoryGateway;

    @Test
    void Given_AValidId_When_CallsDeleteCategory_Should_BeOk() {
        final var aCategory = Category.newCategory("Filme", "A categoria mais assistida", true);
        final var expectedId = aCategory.getId();

        save(aCategory);

        assertEquals(1, categoryRepository.count());

        assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        assertEquals(0, categoryRepository.count());
    }

    @Test
    void Given_AInvalidId_When_CallsDeleteCategory_Should_BeOk() {
        final var expectedId = CategoryID.from("123");

        assertEquals(0, categoryRepository.count());

        assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        assertEquals(0, categoryRepository.count());
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

    private void save(final Category... aCategory) {
        categoryRepository.saveAllAndFlush(
                Arrays.stream(aCategory)
                        .map(CategoryJpaEntity::from)
                        .toList());
    }

}
