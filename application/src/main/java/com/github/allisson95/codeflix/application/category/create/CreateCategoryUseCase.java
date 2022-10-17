package com.github.allisson95.codeflix.application.category.create;

import com.github.allisson95.codeflix.application.UseCase;
import com.github.allisson95.codeflix.domain.validation.handler.Notification;

import io.vavr.control.Either;

public abstract class CreateCategoryUseCase
        extends UseCase<CreateCategoryCommand, Either<Notification, CreateCategoryOutput>> {

}
