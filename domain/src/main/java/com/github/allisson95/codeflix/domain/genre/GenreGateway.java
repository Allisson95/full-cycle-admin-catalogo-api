package com.github.allisson95.codeflix.domain.genre;

import java.util.Optional;

import com.github.allisson95.codeflix.domain.pagination.Pagination;
import com.github.allisson95.codeflix.domain.pagination.SearchQuery;

public interface GenreGateway {

    Genre create(Genre aGenre);

    void deleteById(GenreID anId);

    Optional<Genre> findById(GenreID anId);

    Pagination<Genre> findAll(SearchQuery aQuery);

    Genre update(Genre aGenre);

}
