package com.github.allisson95.codeflix.application.category.create;

import com.github.allisson95.codeflix.domain.category.Category;
import com.github.allisson95.codeflix.domain.category.CategoryID;

public record CreateCategoryOutput(String id) {

    public static CreateCategoryOutput from(final CategoryID anId) {
        return new CreateCategoryOutput(anId.getValue());
    }

    public static CreateCategoryOutput from(final Category aCategory) {
        return new CreateCategoryOutput(aCategory.getId().getValue());
    }

}
