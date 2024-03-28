package com.github.allisson95.codeflix.application.genre.create;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.github.allisson95.codeflix.IntegrationTest;
import com.github.allisson95.codeflix.domain.category.Category;
import com.github.allisson95.codeflix.domain.category.CategoryGateway;
import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.domain.exceptions.NotificationException;
import com.github.allisson95.codeflix.domain.genre.GenreGateway;
import com.github.allisson95.codeflix.infrastructure.genre.persistence.GenreRepository;

@IntegrationTest
class CreateGenreUseCaseIT {

    @SpyBean
    private GenreGateway genreGateway;

    @SpyBean
    private CategoryGateway categoryGateway;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private CreateGenreUseCase useCase;

    @Test
    void Given_AValidCommand_When_CallCreateGenre_Then_ReturnGenreId() {
        final var filmes = categoryGateway.create(
                Category.newCategory("Filmes", null, true));

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of(filmes.getId());

        final var aCommand = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        final var createdGenre = useCase.execute(aCommand);

        assertNotNull(createdGenre);
        assertNotNull(createdGenre.id());

        final var actualGenre = genreRepository.findById(createdGenre.id()).get();

        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertThat(actualGenre.getCategoryIDs(), containsInAnyOrder(expectedCategories.toArray()));
        assertNotNull(actualGenre.getCreatedAt());
        assertNotNull(actualGenre.getUpdatedAt());
        assertNull(actualGenre.getDeletedAt());
    }

    @Test
    void Given_AValidCommandWithoutCategories_When_CallCreateGenre_Then_ReturnGenreId() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var aCommand = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        final var createdGenre = useCase.execute(aCommand);

        assertNotNull(createdGenre);
        assertNotNull(createdGenre.id());

        final var actualGenre = genreRepository.findById(createdGenre.id()).get();

        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertThat(actualGenre.getCategoryIDs(), containsInAnyOrder(expectedCategories.toArray()));
        assertNotNull(actualGenre.getCreatedAt());
        assertNotNull(actualGenre.getUpdatedAt());
        assertNull(actualGenre.getDeletedAt());
    }

    @Test
    void Given_AValidInactiveCommand_When_CallCreateGenre_Then_ReturnGenreId() {
        final var expectedName = "Ação";
        final var expectedIsActive = false;
        final var expectedCategories = List.<CategoryID>of();

        final var aCommand = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        final var createdGenre = useCase.execute(aCommand);

        assertNotNull(createdGenre);
        assertNotNull(createdGenre.id());

        final var actualGenre = genreRepository.findById(createdGenre.id()).get();

        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertThat(actualGenre.getCategoryIDs(), containsInAnyOrder(expectedCategories.toArray()));
        assertNotNull(actualGenre.getCreatedAt());
        assertNotNull(actualGenre.getUpdatedAt());
        assertNotNull(actualGenre.getDeletedAt());
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
    void Given_AInvalidCommandWithEmptyNameAndSomeCategoriesThatDoesNotExists_When_CallCreateGenre_Then_ReturnDomainException() {
        final var seriesId = categoryGateway.create(
                Category.newCategory("Filmes", null, true))
                .getId();
        final var moviesId = CategoryID.from("456");
        final var documentariosId = CategoryID.from("789");

        final var expectedName = " ";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(seriesId, moviesId, documentariosId);
        final var expectedErrorMessageOne = "Some categories could not be found: 456, 789";
        final var expectedErrorMessageTwo = "'name' should not be empty";
        final var expectedErrorCount = 2;

        final var aCommand = CreateGenreCommand.with(expectedName, expectedIsActive, asString(expectedCategories));

        final var actualException = assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        assertNotNull(actualException);
        assertEquals(expectedErrorMessageOne, actualException.getErrors().get(0).message());
        assertEquals(expectedErrorMessageTwo, actualException.getErrors().get(1).message());
        assertEquals(expectedErrorCount, actualException.getErrors().size());

        verify(categoryGateway, times(1)).existsByIds(expectedCategories);
        verify(genreGateway, never()).create(any());
    }

    private List<String> asString(final List<CategoryID> categories) {
        return categories.stream()
                .map(CategoryID::getValue)
                .toList();
    }

}
