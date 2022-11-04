package com.github.allisson95.codeflix.application.category.update;

import static io.vavr.API.Left;
import static io.vavr.API.Try;

import java.util.Objects;
import java.util.function.Supplier;

import com.github.allisson95.codeflix.domain.category.Category;
import com.github.allisson95.codeflix.domain.category.CategoryGateway;
import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.domain.exceptions.DomainException;
import com.github.allisson95.codeflix.domain.exceptions.NotFoundException;
import com.github.allisson95.codeflix.domain.validation.handler.Notification;

import io.vavr.control.Either;

public class DefaultUpdateCategoryUseCase extends UpdateCategoryUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultUpdateCategoryUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Either<Notification, UpdateCategoryOutput> execute(final UpdateCategoryCommand aCommand) {
        final var categoryId = CategoryID.from(aCommand.id());
        final var aName = aCommand.name();
        final var aDescription = aCommand.description();
        final var isActive = aCommand.isActive();

        final var aCategory = this.categoryGateway.findById(categoryId)
                .orElseThrow(notFound(categoryId));

        final var notification = Notification.create();
        aCategory
                .update(aName, aDescription, isActive)
                .validate(notification);

        return notification.hasError() ? Left(notification) : update(aCategory);
    }

    private Supplier<? extends DomainException> notFound(final CategoryID categoryId) {
        return () -> NotFoundException.with(Category.class, categoryId);
    }

    private Either<Notification, UpdateCategoryOutput> update(final Category aCategory) {
        return Try(() -> this.categoryGateway.update(aCategory))
                .toEither()
                .bimap(Notification::create, UpdateCategoryOutput::from);
    }

}
