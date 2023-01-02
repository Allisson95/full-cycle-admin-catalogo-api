package com.github.allisson95.codeflix.application.genre.delete;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.github.allisson95.codeflix.application.UseCaseTest;
import com.github.allisson95.codeflix.domain.genre.Genre;
import com.github.allisson95.codeflix.domain.genre.GenreGateway;
import com.github.allisson95.codeflix.domain.genre.GenreID;

class DeleteGenreUseCaseTest extends UseCaseTest {

    @Mock
    private GenreGateway genreGateway;

    @InjectMocks
    private DefaultDeleteGenreUseCase useCase;

    @Override
    protected List<Object> getMocksForClean() {
        return List.of(genreGateway);
    }

    @Test
    void Given_AValidGenreID_When_CallsDeleteGenre_Should_DeleteGenre() {
        final var aGenre = Genre.newGenre("Ação", true);
        final var expectedId = aGenre.getId();

        doNothing().when(genreGateway).deleteById(expectedId);

        assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        verify(genreGateway, times(1)).deleteById(expectedId);
    }

    @Test
    void Given_AnInvalidGenreID_When_CallsDeleteGenre_Should_BeOK() {
        final var invalidId = GenreID.from("invalid");

        doNothing().when(genreGateway).deleteById(invalidId);

        assertDoesNotThrow(() -> useCase.execute(invalidId.getValue()));

        verify(genreGateway, times(1)).deleteById(invalidId);
    }

    @Test
    void Given_AValidGenreID_When_CallsDeleteGenreAndGatewayThrowsAnException_Should_ReturnError() {
        final var aGenre = Genre.newGenre("Ação", true);
        final var expectedId = aGenre.getId();
        final var expectedErrorMessage = "Gateway error";

        doThrow(new IllegalStateException(expectedErrorMessage)).when(genreGateway).deleteById(expectedId);

        final var exception = assertThrows(IllegalStateException.class, () -> useCase.execute(expectedId.getValue()));

        assertNotNull(exception);
        assertEquals(expectedErrorMessage, exception.getMessage());

        verify(genreGateway, times(1)).deleteById(expectedId);
    }

}
