package com.github.allisson95.codeflix.application.genre.create;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.github.allisson95.codeflix.application.UseCaseTest;
import com.github.allisson95.codeflix.domain.category.CategoryGateway;
import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.domain.exceptions.NotificationException;
import com.github.allisson95.codeflix.domain.genre.GenreGateway;

class CreateGenreUseCaseTest extends UseCaseTest {

    @Mock
    private GenreGateway genreGateway;

    @Mock
    private CategoryGateway categoryGateway;

    @InjectMocks
    private DefaultCreateGenreUseCase useCase;

    @Override
    protected List<Object> getMocksForClean() {
        return List.of(genreGateway, categoryGateway);
    }

    @Test
    void Given_AValidCommand_When_CallCreateGenre_Then_ReturnGenreId() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var aCommand = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        when(genreGateway.create(any())).thenAnswer(returnsFirstArg());

        final var createdGenre = useCase.execute(aCommand);

        assertNotNull(createdGenre);
        assertNotNull(createdGenre.id());

        verify(genreGateway, only())
                .create(argThat(
                        aGenre -> Objects.equals(expectedName, aGenre.getName())
                                && Objects.equals(expectedIsActive, aGenre.isActive())
                                && Objects.equals(expectedCategories, aGenre.getCategories())
                                && Objects.nonNull(aGenre.getId())
                                && Objects.nonNull(aGenre.getCreatedAt())
                                && Objects.nonNull(aGenre.getUpdatedAt())
                                && Objects.isNull(aGenre.getDeletedAt())));
    }

    @Test
    void Given_AValidInactiveCommand_When_CallCreateGenre_Then_ReturnGenreId() {
        final var expectedName = "Ação";
        final var expectedIsActive = false;
        final var expectedCategories = List.<CategoryID>of();

        final var aCommand = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        when(genreGateway.create(any())).thenAnswer(returnsFirstArg());

        final var createdGenre = useCase.execute(aCommand);

        assertNotNull(createdGenre);
        assertNotNull(createdGenre.id());

        verify(genreGateway, only())
                .create(argThat(
                        aGenre -> Objects.equals(expectedName, aGenre.getName())
                                && Objects.equals(expectedIsActive, aGenre.isActive())
                                && Objects.equals(expectedCategories, aGenre.getCategories())
                                && Objects.nonNull(aGenre.getId())
                                && Objects.nonNull(aGenre.getCreatedAt())
                                && Objects.nonNull(aGenre.getUpdatedAt())
                                && Objects.nonNull(aGenre.getDeletedAt())));
    }

    @Test
    void Given_AValidCommandWithCategories_When_CallCreateGenre_Then_ReturnGenreId() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(
                CategoryID.from("123"),
                CategoryID.from("456"));

        final var aCommand = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        when(categoryGateway.existsByIds(any())).thenReturn(expectedCategories);

        when(genreGateway.create(any())).thenAnswer(returnsFirstArg());

        final var createdGenre = useCase.execute(aCommand);

        assertNotNull(createdGenre);
        assertNotNull(createdGenre.id());

        verify(categoryGateway, only()).existsByIds(expectedCategories);

        verify(genreGateway, only())
                .create(argThat(
                        aGenre -> Objects.equals(expectedName, aGenre.getName())
                                && Objects.equals(expectedIsActive, aGenre.isActive())
                                && Objects.equals(expectedCategories, aGenre.getCategories())
                                && Objects.nonNull(aGenre.getId())
                                && Objects.nonNull(aGenre.getCreatedAt())
                                && Objects.nonNull(aGenre.getUpdatedAt())
                                && Objects.isNull(aGenre.getDeletedAt())));
    }

    @Test
    void Given_AInvalidCommandWithNullName_When_CallCreateGenre_Then_ReturnDomainException() {
        final String expectedName = null;
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;

        final var aCommand = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        final var actualException = assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        assertNotNull(actualException);
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        assertEquals(expectedErrorCount, actualException.getErrors().size());

        verify(categoryGateway, never()).existsByIds(any());
        verify(genreGateway, never()).create(any());
    }

    @Test
    void Given_AInvalidCommandWithEmptyName_When_CallCreateGenre_Then_ReturnDomainException() {
        final var expectedName = " ";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();
        final var expectedErrorMessage = "'name' should not be empty";
        final var expectedErrorCount = 1;

        final var aCommand = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        final var actualException = assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        assertNotNull(actualException);
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        assertEquals(expectedErrorCount, actualException.getErrors().size());

        verify(categoryGateway, never()).existsByIds(any());
        verify(genreGateway, never()).create(any());
    }

    @Test
    void Given_AInvalidCommandWithNameWithLengthGreaterThan255_When_CallCreateGenre_Then_ReturnDomainException() {
        final var expectedName = """
                    A prática cotidiana prova que a complexidade dos estudos efetuados facilita a criação das regras de conduta normativas.
                    Nunca é demais lembrar o peso e o significado destes problemas, uma vez que a hegemonia do ambiente político desafia a
                    capacidade de equalização de todos os recursos funcionais envolvidos.
                """;
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();
        final var expectedErrorMessage = "'name' must be between 1 and 255 characteres";
        final var expectedErrorCount = 1;

        final var aCommand = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        final var actualException = assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        assertNotNull(actualException);
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        assertEquals(expectedErrorCount, actualException.getErrors().size());

        verify(categoryGateway, never()).existsByIds(any());
        verify(genreGateway, never()).create(any());
    }

    @Test
    void Given_AValidCommandWithSomeCategoriesThatDoesNotExists_When_CallCreateGenre_Then_ReturnDomainException() {
        final var seriesId = CategoryID.from("123");
        final var moviesId = CategoryID.from("456");
        final var documentariosId = CategoryID.from("789");

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(seriesId, moviesId, documentariosId);
        final var expectedErrorMessage = "Some categories could not be found: 456, 789";
        final var expectedErrorCount = 1;

        final var aCommand = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        when(categoryGateway.existsByIds(any())).thenReturn(List.of(seriesId));

        final var actualException = assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        assertNotNull(actualException);
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        assertEquals(expectedErrorCount, actualException.getErrors().size());

        verify(categoryGateway, only()).existsByIds(expectedCategories);
        verify(genreGateway, never()).create(any());
    }

    @Test
    void Given_AInvalidCommandWithEmptyNameAndSomeCategoriesThatDoesNotExists_When_CallCreateGenre_Then_ReturnDomainException() {
        final var seriesId = CategoryID.from("123");
        final var moviesId = CategoryID.from("456");
        final var documentariosId = CategoryID.from("789");

        final var expectedName = " ";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(seriesId, moviesId, documentariosId);
        final var expectedErrorMessageOne = "Some categories could not be found: 456, 789";
        final var expectedErrorMessageTwo = "'name' should not be empty";
        final var expectedErrorCount = 2;

        final var aCommand = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        when(categoryGateway.existsByIds(any())).thenReturn(List.of(seriesId));

        final var actualException = assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        assertNotNull(actualException);
        assertEquals(expectedErrorMessageOne, actualException.getErrors().get(0).message());
        assertEquals(expectedErrorMessageTwo, actualException.getErrors().get(1).message());
        assertEquals(expectedErrorCount, actualException.getErrors().size());

        verify(categoryGateway, only()).existsByIds(expectedCategories);
        verify(genreGateway, never()).create(any());
    }

}
