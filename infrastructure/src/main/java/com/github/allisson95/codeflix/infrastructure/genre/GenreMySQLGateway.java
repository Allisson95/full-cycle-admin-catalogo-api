package com.github.allisson95.codeflix.infrastructure.genre;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.github.allisson95.codeflix.domain.genre.Genre;
import com.github.allisson95.codeflix.domain.genre.GenreGateway;
import com.github.allisson95.codeflix.domain.genre.GenreID;
import com.github.allisson95.codeflix.domain.pagination.Pagination;
import com.github.allisson95.codeflix.domain.pagination.SearchQuery;
import com.github.allisson95.codeflix.infrastructure.genre.persistence.GenreJpaEntity;
import com.github.allisson95.codeflix.infrastructure.genre.persistence.GenreRepository;

@Component
public class GenreMySQLGateway implements GenreGateway {

    private final GenreRepository genreRepository;

    public GenreMySQLGateway(final GenreRepository genreRepository) {
        this.genreRepository = Objects.requireNonNull(genreRepository);
    }

    @Override
    public Genre create(final Genre aGenre) {
        return save(aGenre);
    }

    @Override
    public void deleteById(GenreID anId) {
        // TODO Auto-generated method stub

    }

    @Override
    public Optional<Genre> findById(GenreID anId) {
        // TODO Auto-generated method stub
        return Optional.empty();
    }

    @Override
    public Pagination<Genre> findAll(SearchQuery aQuery) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Genre update(final Genre aGenre) {
        return save(aGenre);
    }

    private Genre save(final Genre aGenre) {
        return this.genreRepository
                .save(GenreJpaEntity.from(aGenre))
                .toAggregate();
    }

}
