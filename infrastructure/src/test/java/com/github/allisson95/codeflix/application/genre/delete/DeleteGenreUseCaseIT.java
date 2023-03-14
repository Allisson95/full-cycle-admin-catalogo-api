package com.github.allisson95.codeflix.application.genre.delete;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.github.allisson95.codeflix.IntegrationTest;
import com.github.allisson95.codeflix.domain.genre.Genre;
import com.github.allisson95.codeflix.domain.genre.GenreGateway;
import com.github.allisson95.codeflix.domain.genre.GenreID;
import com.github.allisson95.codeflix.infrastructure.genre.persistence.GenreRepository;

@IntegrationTest
class DeleteGenreUseCaseIT {

    @SpyBean
    private GenreGateway genreGateway;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private DeleteGenreUseCase useCase;

    @Test
    void Given_AValidGenreID_When_CallsDeleteGenre_Should_DeleteGenre() {
        final var aGenre = genreGateway.create(Genre.newGenre("Ação", true));
        final var expectedId = aGenre.getId();

        assertEquals(1, genreRepository.count());

        assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        assertEquals(0, genreRepository.count());

        verify(genreGateway, times(1)).deleteById(expectedId);
    }

    @Test
    void Given_AnInvalidGenreID_When_CallsDeleteGenre_Should_BeOK() {
        genreGateway.create(Genre.newGenre("Ação", true));
        final var invalidId = GenreID.from("invalid");

        assertEquals(1, genreRepository.count());

        assertDoesNotThrow(() -> useCase.execute(invalidId.getValue()));

        assertEquals(1, genreRepository.count());

        verify(genreGateway, times(1)).deleteById(invalidId);
    }

}
