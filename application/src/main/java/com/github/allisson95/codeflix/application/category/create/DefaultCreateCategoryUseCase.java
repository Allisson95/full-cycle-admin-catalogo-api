package com.github.allisson95.codeflix.application.category.create;

import static io.vavr.API.Left;
import static io.vavr.API.Try;

import java.util.Objects;

import com.github.allisson95.codeflix.domain.category.Category;
import com.github.allisson95.codeflix.domain.category.CategoryGateway;
import com.github.allisson95.codeflix.domain.validation.handler.Notification;

import io.vavr.control.Either;

public class DefaultCreateCategoryUseCase extends CreateCategoryUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultCreateCategoryUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Either<Notification, CreateCategoryOutput> execute(final CreateCategoryCommand aCommand) {
        final var notification = Notification.create();

        final var newCategory = Category.newCategory(aCommand.name(), aCommand.description(), aCommand.isActive());

        newCategory.validate(notification);

        return notification.hasError() ? Left(notification) : create(newCategory);
    }

    private Either<Notification, CreateCategoryOutput> create(final Category newCategory) {
        return Try(() -> this.categoryGateway.create(newCategory))
                .toEither()
                .bimap(Notification::create, CreateCategoryOutput::from);
    }

}
