package com.github.allisson95.codeflix.infrastructure.api.controllers;

import static org.springframework.web.util.UriComponentsBuilder.fromPath;

import java.util.Objects;
import java.util.function.Function;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.github.allisson95.codeflix.application.category.create.CreateCategoryCommand;
import com.github.allisson95.codeflix.application.category.create.CreateCategoryOutput;
import com.github.allisson95.codeflix.application.category.create.CreateCategoryUseCase;
import com.github.allisson95.codeflix.domain.pagination.Pagination;
import com.github.allisson95.codeflix.domain.validation.handler.Notification;
import com.github.allisson95.codeflix.infrastructure.api.CategoryAPI;
import com.github.allisson95.codeflix.infrastructure.category.models.CreateCategoryApiInput;

@RestController
public class CategoryController implements CategoryAPI {

    private final CreateCategoryUseCase createCategoryUseCase;

    public CategoryController(final CreateCategoryUseCase createCategoryUseCase) {
        this.createCategoryUseCase = Objects.requireNonNull(createCategoryUseCase);
    }

    @Override
    public ResponseEntity<?> createCategory(final CreateCategoryApiInput input) {
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
    public Pagination<?> listCategories(
            final String search,
            final int page,
            final int perPage,
            final String sort,
            final String dir) {
        return null;
    }

}
