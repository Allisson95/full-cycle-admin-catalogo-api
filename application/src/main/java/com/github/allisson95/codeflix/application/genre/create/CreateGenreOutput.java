package com.github.allisson95.codeflix.application.genre.create;

import com.github.allisson95.codeflix.domain.genre.Genre;
import com.github.allisson95.codeflix.domain.genre.GenreID;

public record CreateGenreOutput(String id) {

    public static CreateGenreOutput from(final GenreID anId) {
        return new CreateGenreOutput(anId.getValue());
    }

    public static CreateGenreOutput from(final Genre aGenre) {
        return new CreateGenreOutput(aGenre.getId().getValue());
    }

}
