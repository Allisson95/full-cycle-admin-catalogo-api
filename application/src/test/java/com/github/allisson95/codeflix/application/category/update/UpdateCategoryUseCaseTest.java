package com.github.allisson95.codeflix.application.category.update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Objects;
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
import com.github.allisson95.codeflix.domain.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
class UpdateCategoryUseCaseTest {

    @Mock
    private CategoryGateway categoryGateway;

    @InjectMocks
    private DefaultUpdateCategoryUseCase useCase;

    @BeforeEach
    void cleanUp() {
        reset(categoryGateway);
    }

    @Test
    void Given_AValidCommand_When_CallUpdateCategory_Then_ReturnCategoryId() {
        final var aCategory = Category.newCategory("Film", null, false);

        final var expectedId = aCategory.getId();
        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCommand = UpdateCategoryCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedIsActive);

        when(categoryGateway.findById(expectedId)).thenReturn(Optional.of(Category.with(aCategory)));
        when(categoryGateway.update(any())).thenAnswer(returnsFirstArg());

        final var updatedCategory = useCase.execute(aCommand).get();

        assertNotNull(updatedCategory);
        assertNotNull(updatedCategory.id());

        verify(categoryGateway, times(1)).findById(expectedId);

        verify(categoryGateway, times(1))
                .update(argThat(
                        anUpdatedCategory -> Objects.equals(expectedName, anUpdatedCategory.getName())
                                && Objects.equals(expectedDescription, anUpdatedCategory.getDescription())
                                && Objects.equals(expectedIsActive, anUpdatedCategory.isActive())
                                && Objects.equals(expectedId, anUpdatedCategory.getId())
                                && Objects.equals(aCategory.getCreatedAt(), anUpdatedCategory.getCreatedAt())
                                && aCategory.getUpdatedAt().isBefore(anUpdatedCategory.getUpdatedAt())
                                && Objects.isNull(anUpdatedCategory.getDeletedAt())));
    }

    @Test
    void Given_AnInvalidName_When_CallUpdateCategory_Then_ReturnDomainException() {
        final var aCategory = Category.newCategory("Filme", "A categoria mais assistida", false);

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

        when(categoryGateway.findById(expectedId)).thenReturn(Optional.of(Category.with(aCategory)));

        final var notification = useCase.execute(aCommand).getLeft();

        assertEquals(expectedErrorCount, notification.getErrors().size());
        assertEquals(expectedErrorMessage, notification.firstError().message());

        verify(categoryGateway, times(1)).findById(expectedId);

        verify(categoryGateway, times(0)).update(any());
    }

    @Test
    void Given_AValidInactivateCommand_When_CallUpdateCategory_Then_ReturnInactiveCategoryId() {
        final var aCategory = Category.newCategory("Filme", "A categoria mais assistida", true);

        final var expectedId = aCategory.getId();
        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var aCommand = UpdateCategoryCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedIsActive);

        when(categoryGateway.findById(expectedId)).thenReturn(Optional.of(Category.with(aCategory)));
        when(categoryGateway.update(any())).thenAnswer(returnsFirstArg());

        assertTrue(aCategory.isActive());
        assertNull(aCategory.getDeletedAt());

        final var updatedCategory = useCase.execute(aCommand).get();

        assertNotNull(updatedCategory);
        assertNotNull(updatedCategory.id());

        verify(categoryGateway, times(1)).findById(expectedId);

        verify(categoryGateway, times(1))
                .update(argThat(
                        anUpdatedCategory -> Objects.equals(expectedName, anUpdatedCategory.getName())
                                && Objects.equals(expectedDescription, anUpdatedCategory.getDescription())
                                && Objects.equals(expectedIsActive, anUpdatedCategory.isActive())
                                && Objects.equals(expectedId, anUpdatedCategory.getId())
                                && Objects.equals(aCategory.getCreatedAt(), anUpdatedCategory.getCreatedAt())
                                && aCategory.getUpdatedAt().isBefore(anUpdatedCategory.getUpdatedAt())
                                && Objects.nonNull(anUpdatedCategory.getDeletedAt())));
    }

    @Test
    void Given_AValidCommand_When_GatewayThrowsRandomException_Then_ReturnAException() {
        final var aCategory = Category.newCategory("Filme", null, true);

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

        when(categoryGateway.findById(expectedId)).thenReturn(Optional.of(Category.with(aCategory)));
        when(categoryGateway.update(any())).thenThrow(new IllegalStateException(expectedErrorMessage));

        final var notification = useCase.execute(aCommand).getLeft();

        assertEquals(expectedErrorCount, notification.getErrors().size());
        assertEquals(expectedErrorMessage, notification.firstError().message());

        verify(categoryGateway, times(1)).findById(expectedId);

        verify(categoryGateway, times(1))
                .update(argThat(
                        anUpdatedCategory -> Objects.equals(expectedName, anUpdatedCategory.getName())
                                && Objects.equals(expectedDescription, anUpdatedCategory.getDescription())
                                && Objects.equals(expectedIsActive, anUpdatedCategory.isActive())
                                && Objects.equals(expectedId, anUpdatedCategory.getId())
                                && Objects.equals(aCategory.getCreatedAt(), anUpdatedCategory.getCreatedAt())
                                && aCategory.getUpdatedAt().isBefore(anUpdatedCategory.getUpdatedAt())
                                && Objects.isNull(anUpdatedCategory.getDeletedAt())));
    }

    @Test
    void Given_ACommandWithInvalidID_When_CallUpdateCategory_Then_ReturnNotFoundException() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;
        final var expectedId = "123";
        final var expectedErrorMessage = "Category with id 123 was not found";

        final var aCommand = UpdateCategoryCommand.with(
                expectedId,
                expectedName,
                expectedDescription,
                expectedIsActive);

        when(categoryGateway.findById(CategoryID.from(expectedId))).thenReturn(Optional.empty());

        final var actualException = assertThrows(NotFoundException.class, () -> useCase.execute(aCommand));

        assertEquals(expectedErrorMessage, actualException.getMessage());

        verify(categoryGateway, times(1)).findById(CategoryID.from(expectedId));

        verify(categoryGateway, times(0)).update(any());
    }

}
