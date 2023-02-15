package com.github.allisson95.codeflix.infrastructure.genre;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.github.allisson95.codeflix.domain.genre.Genre;
import com.github.allisson95.codeflix.domain.genre.GenreGateway;
import com.github.allisson95.codeflix.domain.genre.GenreID;
import com.github.allisson95.codeflix.domain.pagination.Pagination;
import com.github.allisson95.codeflix.domain.pagination.SearchQuery;

@Component
public class GenreMySQLGateway implements GenreGateway {

    @Override
    public Genre create(Genre aGenre) {
        // TODO Auto-generated method stub
        return null;
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
    public Genre update(Genre aCategory) {
        // TODO Auto-generated method stub
        return null;
    }

}
