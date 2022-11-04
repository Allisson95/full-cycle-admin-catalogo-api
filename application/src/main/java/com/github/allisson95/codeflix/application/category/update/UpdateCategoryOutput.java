package com.github.allisson95.codeflix.application.category.update;

import com.github.allisson95.codeflix.domain.category.Category;
import com.github.allisson95.codeflix.domain.category.CategoryID;

public record UpdateCategoryOutput(String id) {

    public static UpdateCategoryOutput from(final CategoryID anId) {
        return new UpdateCategoryOutput(anId.getValue());
    }

    public static UpdateCategoryOutput from(final Category aCategory) {
        return new UpdateCategoryOutput(aCategory.getId().getValue());
    }

}
