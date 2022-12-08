package com.github.allisson95.codeflix.infrastructure.api.controllers;

import static org.springframework.web.util.UriComponentsBuilder.fromPath;

import java.util.Objects;
import java.util.function.Function;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.github.allisson95.codeflix.application.category.create.CreateCategoryCommand;
import com.github.allisson95.codeflix.application.category.create.CreateCategoryOutput;
import com.github.allisson95.codeflix.application.category.create.CreateCategoryUseCase;
import com.github.allisson95.codeflix.application.category.delete.DeleteCategoryUseCase;
import com.github.allisson95.codeflix.application.category.retrieve.get.GetCategoryByIdUseCase;
import com.github.allisson95.codeflix.application.category.retrieve.list.ListCategoriesUseCase;
import com.github.allisson95.codeflix.application.category.update.UpdateCategoryCommand;
import com.github.allisson95.codeflix.application.category.update.UpdateCategoryOutput;
import com.github.allisson95.codeflix.application.category.update.UpdateCategoryUseCase;
import com.github.allisson95.codeflix.domain.pagination.SearchQuery;
import com.github.allisson95.codeflix.domain.pagination.Pagination;
import com.github.allisson95.codeflix.domain.validation.handler.Notification;
import com.github.allisson95.codeflix.infrastructure.api.CategoryAPI;
import com.github.allisson95.codeflix.infrastructure.category.models.CategoryResponse;
import com.github.allisson95.codeflix.infrastructure.category.models.CategoryListResponse;
import com.github.allisson95.codeflix.infrastructure.category.models.CreateCategoryRequest;
import com.github.allisson95.codeflix.infrastructure.category.models.UpdateCategoryRequest;
import com.github.allisson95.codeflix.infrastructure.category.presenters.CategoryApiPresenter;

@RestController
public class CategoryController implements CategoryAPI {

    private final CreateCategoryUseCase createCategoryUseCase;
    private final GetCategoryByIdUseCase getCategoryByIdUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;
    private final ListCategoriesUseCase listCategoriesUseCase;

    public CategoryController(
            final CreateCategoryUseCase createCategoryUseCase,
            final GetCategoryByIdUseCase getCategoryByIdUseCase,
            final UpdateCategoryUseCase updateCategoryUseCase,
            final DeleteCategoryUseCase deleteCategoryUseCase,
            final ListCategoriesUseCase listCategoriesUseCase) {
        this.createCategoryUseCase = Objects.requireNonNull(createCategoryUseCase);
        this.getCategoryByIdUseCase = Objects.requireNonNull(getCategoryByIdUseCase);
        this.updateCategoryUseCase = Objects.requireNonNull(updateCategoryUseCase);
        this.deleteCategoryUseCase = Objects.requireNonNull(deleteCategoryUseCase);
        this.listCategoriesUseCase = Objects.requireNonNull(listCategoriesUseCase);
    }

    @Override
    public ResponseEntity<?> createCategory(final CreateCategoryRequest input) {
        final var aCommand = CreateCategoryCommand.with(
                input.name(),
                input.description(),
                input.active() != null ? input.active() : true);

        final Function<Notification, ResponseEntity<?>> onError = ResponseEntity.unprocessableEntity()::body;

        final Function<CreateCategoryOutput, ResponseEntity<?>> onSuccess = output -> {
            final var location = fromPath("/categories/{categoryID}").build(output.id());

            return ResponseEntity
                    .created(location)
                    .body(output);
        };

        return this.createCategoryUseCase.execute(aCommand)
                .fold(onError, onSuccess);
    }

    @Override
    public Pagination<CategoryListResponse> listCategories(
            final String search,
            final int page,
            final int perPage,
            final String sort,
            final String dir) {
        return this.listCategoriesUseCase
                .execute(new SearchQuery(page, perPage, search, sort, dir))
                .map(CategoryApiPresenter::present);
    }

    @Override
    public CategoryResponse getById(final String categoryId) {
        return CategoryApiPresenter.present(this.getCategoryByIdUseCase.execute(categoryId));
    }

    @Override
    public ResponseEntity<?> updateById(final String categoryId, final UpdateCategoryRequest input) {
        final var aCommand = UpdateCategoryCommand.with(
                categoryId,
                input.name(),
                input.description(),
                input.active() != null ? input.active() : true);

        final Function<Notification, ResponseEntity<?>> onError = ResponseEntity.unprocessableEntity()::body;

        final Function<UpdateCategoryOutput, ResponseEntity<?>> onSuccess = ResponseEntity::ok;

        return this.updateCategoryUseCase.execute(aCommand)
                .fold(onError, onSuccess);
    }

    @Override
    public void deleteById(final String categoryId) {
        this.deleteCategoryUseCase.execute(categoryId);
    }

}
