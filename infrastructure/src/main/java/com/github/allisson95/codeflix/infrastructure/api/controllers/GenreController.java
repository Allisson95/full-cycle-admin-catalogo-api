package com.github.allisson95.codeflix.infrastructure.api.controllers;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.allisson95.codeflix.application.genre.create.CreateGenreCommand;
import com.github.allisson95.codeflix.application.genre.create.CreateGenreUseCase;
import com.github.allisson95.codeflix.application.genre.delete.DeleteGenreUseCase;
import com.github.allisson95.codeflix.application.genre.retrieve.get.GetGenreByIdUseCase;
import com.github.allisson95.codeflix.application.genre.update.UpdateGenreCommand;
import com.github.allisson95.codeflix.application.genre.update.UpdateGenreUseCase;
import com.github.allisson95.codeflix.domain.pagination.Pagination;
import com.github.allisson95.codeflix.infrastructure.api.GenreAPI;
import com.github.allisson95.codeflix.infrastructure.genre.models.CreateGenreRequest;
import com.github.allisson95.codeflix.infrastructure.genre.models.GenreListResponse;
import com.github.allisson95.codeflix.infrastructure.genre.models.GenreResponse;
import com.github.allisson95.codeflix.infrastructure.genre.models.UpdateGenreRequest;
import com.github.allisson95.codeflix.infrastructure.genre.presenters.GenreApiPresenter;

@RestController
public class GenreController implements GenreAPI {

    private final CreateGenreUseCase createGenreUseCase;
    private final GetGenreByIdUseCase getGenreByIdUseCase;
    private final UpdateGenreUseCase updateGenreUseCase;
    private final DeleteGenreUseCase deleteGenreUseCase;

    public GenreController(
        final CreateGenreUseCase createGenreUseCase,
        final GetGenreByIdUseCase getGenreByIdUseCase,
        final UpdateGenreUseCase updateGenreUseCase,
        final DeleteGenreUseCase deleteGenreUseCase
    ) {
        this.createGenreUseCase = Objects.requireNonNull(createGenreUseCase);
        this.getGenreByIdUseCase = Objects.requireNonNull(getGenreByIdUseCase);
        this.updateGenreUseCase = Objects.requireNonNull(updateGenreUseCase);
        this.deleteGenreUseCase = Objects.requireNonNull(deleteGenreUseCase);
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
        return GenreApiPresenter.present(this.getGenreByIdUseCase.execute(id));
    }

    @Override
    public ResponseEntity<?> updateById(final String id, final UpdateGenreRequest input) {
        final var aCommand = UpdateGenreCommand.with(
                id,
                input.name(),
                input.active(),
                input.categories());

        final var output = this.updateGenreUseCase.execute(aCommand);

        return ResponseEntity.ok(output);
    }

    @Override
    public void deleteById(final String id) {
        this.deleteGenreUseCase.execute(id);
    }

}
