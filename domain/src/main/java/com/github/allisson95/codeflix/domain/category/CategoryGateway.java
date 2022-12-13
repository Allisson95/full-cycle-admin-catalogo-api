package com.github.allisson95.codeflix.domain.category;

import java.util.List;
import java.util.Optional;

import com.github.allisson95.codeflix.domain.pagination.Pagination;
import com.github.allisson95.codeflix.domain.pagination.SearchQuery;

public interface CategoryGateway {

    Category create(Category aCategory);

    void deleteById(CategoryID anId);

    Optional<Category> findById(CategoryID anId);

    Pagination<Category> findAll(SearchQuery aQuery);

    Category update(Category aCategory);

    List<CategoryID> existsByIds(Iterable<CategoryID> ids);

}
