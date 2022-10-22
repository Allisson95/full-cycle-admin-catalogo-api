package com.github.allisson95.codeflix.infrastructure.category;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.github.allisson95.codeflix.domain.category.Category;
import com.github.allisson95.codeflix.domain.category.CategoryGateway;
import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.domain.category.CategorySearchQuery;
import com.github.allisson95.codeflix.domain.pagination.Pagination;
import com.github.allisson95.codeflix.infrastructure.category.persistence.CategoryJpaEntity;
import com.github.allisson95.codeflix.infrastructure.category.persistence.CategoryRepository;

@Component
public class CategoryMySQLGateway implements CategoryGateway {

    private final CategoryRepository repository;

    public CategoryMySQLGateway(final CategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Category create(Category aCategory) {
        return this.repository.save(CategoryJpaEntity.from(aCategory)).toAggregate();
    }

    @Override
    public void deleteById(CategoryID anId) {
        // TODO Auto-generated method stub

    }

    @Override
    public Optional<Category> findById(CategoryID anId) {
        // TODO Auto-generated method stub
        return Optional.empty();
    }

    @Override
    public Pagination<Category> findAll(CategorySearchQuery aQuery) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Category update(Category aCategory) {
        // TODO Auto-generated method stub
        return null;
    }

}
