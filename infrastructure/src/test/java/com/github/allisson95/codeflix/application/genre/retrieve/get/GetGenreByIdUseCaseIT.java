package com.github.allisson95.codeflix.application.genre.retrieve.get;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import com.github.allisson95.codeflix.domain.exceptions.NotFoundException;
import com.github.allisson95.codeflix.domain.genre.Genre;
import com.github.allisson95.codeflix.domain.genre.GenreGateway;
import com.github.allisson95.codeflix.domain.genre.GenreID;

@IntegrationTest
class GetGenreByIdUseCaseIT {

    @Autowired
    private GetGenreByIdUseCase useCase;

    @SpyBean
    private GenreGateway genreGateway;

    @SpyBean
    private CategoryGateway categoryGateway;

    @Test
    void Given_AValidId_WhenCallsGetGenreById_Should_ReturnGenre() {
        final var filmes = categoryGateway.create(
                Category.newCategory("Filmes", null, true));
        final var series = categoryGateway.create(
                Category.newCategory("Séries", null, true));

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId(), series.getId());

        final var aGenre = genreGateway.create(
                Genre.newGenre(expectedName, expectedIsActive)
                        .addCategories(expectedCategories));

        final var expectedId = aGenre.getId();

        final var actualGenre = useCase.execute(expectedId.getValue());

        assertNotNull(actualGenre);
        assertEquals(expectedId.getValue(), actualGenre.id());
        assertEquals(expectedName, actualGenre.name());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertThat(actualGenre.categories(), containsInAnyOrder(asString(expectedCategories).toArray()));
        assertEquals(aGenre.getCreatedAt(), actualGenre.createdAt());
        assertEquals(aGenre.getUpdatedAt(), actualGenre.updatedAt());
        assertEquals(aGenre.getDeletedAt(), actualGenre.deletedAt());
    }

    @Test
    void Given_AInvalidId_WhenCallsGetGenreById_Should_ReturnNotFound() {
        final var expectedId = GenreID.from("123");
        final var expectedErrorMessage = "Genre with id 123 was not found";

        final var exception = assertThrows(NotFoundException.class, () -> useCase.execute(expectedId.getValue()));

        assertEquals(expectedErrorMessage, exception.getMessage());

        verify(genreGateway, times(1)).findById(expectedId);
    }

    private List<String> asString(final List<CategoryID> categories) {
        return categories.stream()
                .map(CategoryID::getValue)
                .toList();
    }

}
