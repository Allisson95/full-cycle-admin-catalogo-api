package com.github.allisson95.codeflix.infrastructure.category.presenters;

import com.github.allisson95.codeflix.application.category.retrieve.get.CategoryOutput;
import com.github.allisson95.codeflix.infrastructure.category.models.CategoryApiOutput;

public interface CategoryApiPresenter {

    static CategoryApiOutput present(final CategoryOutput output) {
        return new CategoryApiOutput(
                output.id().getValue(),
                output.name(),
                output.description(),
                output.isActive(),
                output.createdAt(),
                output.updatedAt(),
                output.deletedAt());
    }

}
