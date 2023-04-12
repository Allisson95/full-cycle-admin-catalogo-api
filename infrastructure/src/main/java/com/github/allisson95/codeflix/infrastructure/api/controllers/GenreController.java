package com.github.allisson95.codeflix.infrastructure.api.controllers;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.allisson95.codeflix.application.genre.create.CreateGenreCommand;
import com.github.allisson95.codeflix.application.genre.create.CreateGenreUseCase;
import com.github.allisson95.codeflix.domain.pagination.Pagination;
import com.github.allisson95.codeflix.infrastructure.api.GenreAPI;
import com.github.allisson95.codeflix.infrastructure.genre.models.CreateGenreRequest;
import com.github.allisson95.codeflix.infrastructure.genre.models.GenreListResponse;
import com.github.allisson95.codeflix.infrastructure.genre.models.GenreResponse;
import com.github.allisson95.codeflix.infrastructure.genre.models.UpdateGenreRequest;

@RestController
public class GenreController implements GenreAPI {

    private final CreateGenreUseCase createGenreUseCase;

    public GenreController(final CreateGenreUseCase createGenreUseCase) {
        this.createGenreUseCase = Objects.requireNonNull(createGenreUseCase);
    }

    @Override
    public ResponseEntity<?> create(final CreateGenreRequest input) {
        final var aCommand = CreateGenreCommand.with(
                input.name(),
                input.active(),
                input.categories());

        final var output = this.createGenreUseCase.execute(aCommand);

        final var location = UriComponentsBuilder.fromPath("/genres/{genreId}").build(output.id());

        return ResponseEntity.created(location).body(output);
    }

    @Override
    public Pagination<GenreListResponse> list(
            final String search,
            final int page,
            final int perPage,
            final String sort,
            final String dir) {
        throw new UnsupportedOperationException("Unimplemented method 'list'");
    }

    @Override
    public GenreResponse getById(final String id) {
        throw new UnsupportedOperationException("Unimplemented method 'getById'");
    }

    @Override
    public ResponseEntity<?> updateById(final String id, final UpdateGenreRequest input) {
        throw new UnsupportedOperationException("Unimplemented method 'updateById'");
    }

    @Override
    public void deleteById(final String id) {
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
    }

}
