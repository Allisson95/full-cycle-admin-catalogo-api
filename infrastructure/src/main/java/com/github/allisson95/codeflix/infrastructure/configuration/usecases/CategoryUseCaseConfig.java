package com.github.allisson95.codeflix.infrastructure.configuration.usecases;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.allisson95.codeflix.application.category.create.CreateCategoryUseCase;
import com.github.allisson95.codeflix.application.category.create.DefaultCreateCategoryUseCase;
import com.github.allisson95.codeflix.application.category.delete.DefaultDeleteCategoryUseCase;
import com.github.allisson95.codeflix.application.category.delete.DeleteCategoryUseCase;
import com.github.allisson95.codeflix.application.category.retrieve.get.DefaultGetCategoryByIdUseCase;
import com.github.allisson95.codeflix.application.category.retrieve.get.GetCategoryByIdUseCase;
import com.github.allisson95.codeflix.application.category.retrieve.list.DefaultListCategoriesUseCase;
import com.github.allisson95.codeflix.application.category.retrieve.list.ListCategoriesUseCase;
import com.github.allisson95.codeflix.application.category.update.DefaultUpdateCategoryUseCase;
import com.github.allisson95.codeflix.application.category.update.UpdateCategoryUseCase;
import com.github.allisson95.codeflix.domain.category.CategoryGateway;

@Configuration
public class CategoryUseCaseConfig {

    private final CategoryGateway categoryGateway;

    public CategoryUseCaseConfig(final CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    @Bean
    public CreateCategoryUseCase createCategoryUseCase() {
        return new DefaultCreateCategoryUseCase(this.categoryGateway);
    }

    @Bean
    public DeleteCategoryUseCase deleteCategoryUseCase() {
        return new DefaultDeleteCategoryUseCase(this.categoryGateway);
    }

    @Bean
    public GetCategoryByIdUseCase getCategoryByIdUseCase() {
        return new DefaultGetCategoryByIdUseCase(this.categoryGateway);
    }

    @Bean
    public ListCategoriesUseCase listCategoriesUseCase() {
        return new DefaultListCategoriesUseCase(this.categoryGateway);
    }

    @Bean
    public UpdateCategoryUseCase updateCategoryUseCase() {
        return new DefaultUpdateCategoryUseCase(this.categoryGateway);
    }

}
