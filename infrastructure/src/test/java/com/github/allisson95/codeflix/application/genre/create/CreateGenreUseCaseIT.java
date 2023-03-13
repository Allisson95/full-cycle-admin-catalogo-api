package com.github.allisson95.codeflix.application.genre.create;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.github.allisson95.codeflix.IntegrationTest;
import com.github.allisson95.codeflix.domain.category.Category;
import com.github.allisson95.codeflix.domain.category.CategoryGateway;
import com.github.allisson95.codeflix.domain.category.CategoryID;
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

        verify(genreGateway, only())
                .create(argThat(
                        aGenre -> Objects.equals(expectedName, aGenre.getName())
                                && Objects.equals(expectedIsActive, aGenre.isActive())
                                && Objects.equals(expectedCategories, aGenre.getCategories())
                                && Objects.nonNull(aGenre.getId())
                                && Objects.nonNull(aGenre.getCreatedAt())
                                && Objects.nonNull(aGenre.getUpdatedAt())
                                && Objects.isNull(aGenre.getDeletedAt())));

        final var actualGenre = genreRepository.findById(createdGenre.id()).get();

        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertThat(actualGenre.getCategoryIDs(), containsInAnyOrder(expectedCategories.toArray()));
        assertNotNull(actualGenre.getCreatedAt());
        assertNotNull(actualGenre.getUpdatedAt());
        assertNull(actualGenre.getDeletedAt());
    }

    private List<String> asString(final List<CategoryID> categories) {
        return categories.stream()
                .map(CategoryID::getValue)
                .toList();
    }

}
