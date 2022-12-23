package com.github.allisson95.codeflix.application.genre.update;

import com.github.allisson95.codeflix.domain.genre.Genre;

public record UpdateGenreOutput(String id) {

    public static UpdateGenreOutput from(final Genre aGenre) {
        return new UpdateGenreOutput(aGenre.getId().getValue());
    }

}
