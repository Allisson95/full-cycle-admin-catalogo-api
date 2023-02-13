package com.github.allisson95.codeflix.application.genre.retrieve.list;

import java.time.Instant;

import com.github.allisson95.codeflix.domain.genre.Genre;

public record GenreListOutput(
        String id,
        String name,
        boolean isActive,
        Instant createdAt,
        Instant deletedAt) {

    public static GenreListOutput from(final Genre aGenre) {
        return new GenreListOutput(
                aGenre.getId().getValue(),
                aGenre.getName(),
                aGenre.isActive(),
                aGenre.getCreatedAt(),
                aGenre.getDeletedAt());
    }

}
