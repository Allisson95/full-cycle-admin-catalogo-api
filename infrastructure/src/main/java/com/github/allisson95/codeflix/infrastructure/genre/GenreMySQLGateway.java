package com.github.allisson95.codeflix.infrastructure.genre;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.github.allisson95.codeflix.domain.genre.Genre;
import com.github.allisson95.codeflix.domain.genre.GenreGateway;
import com.github.allisson95.codeflix.domain.genre.GenreID;
import com.github.allisson95.codeflix.domain.pagination.Pagination;
import com.github.allisson95.codeflix.domain.pagination.SearchQuery;
import com.github.allisson95.codeflix.infrastructure.genre.persistence.GenreJpaEntity;
import com.github.allisson95.codeflix.infrastructure.genre.persistence.GenreRepository;
import com.github.allisson95.codeflix.infrastructure.utils.SpecificationUtils;

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
    public void deleteById(final GenreID anId) {
        final String anIdValue = anId.getValue();
        if (this.genreRepository.existsById(anIdValue)) {
            this.genreRepository.deleteById(anIdValue);
        }
    }

    @Override
    public Optional<Genre> findById(final GenreID anId) {
        return this.genreRepository.findById(anId.getValue())
                .map(GenreJpaEntity::toAggregate);
    }

    @Override
    public Pagination<Genre> findAll(final SearchQuery aQuery) {
        final var page = PageRequest.of(
                aQuery.page(),
                aQuery.perPage(),
                Sort.by(Direction.fromString(aQuery.direction()), aQuery.sort()));

        final var spec = Optional.ofNullable(aQuery.terms())
                .filter(terms -> !terms.isBlank())
                .map(terms -> SpecificationUtils.<GenreJpaEntity>like("name", terms))
                .orElse(null);

        final var pageResult = this.genreRepository.findAll(Specification.where(spec), page);

        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(GenreJpaEntity::toAggregate).toList());
    }

    @Override
    public Genre update(final Genre aGenre) {
        return save(aGenre);
    }

    @Override
    public List<GenreID> existsByIds(final Iterable<GenreID> genreIDs) {
        final var ids = StreamSupport.stream(genreIDs.spliterator(), false)
                .map(GenreID::getValue)
                .toList();

        return this.genreRepository.existsByIds(ids)
                .stream()
                .map(GenreID::from)
                .toList();
    }

    private Genre save(final Genre aGenre) {
        return this.genreRepository
                .save(GenreJpaEntity.from(aGenre))
                .toAggregate();
    }

}
