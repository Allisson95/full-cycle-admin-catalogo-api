package com.github.allisson95.codeflix.infrastructure.configuration.usecases;

import java.util.Objects;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.allisson95.codeflix.application.genre.create.CreateGenreUseCase;
import com.github.allisson95.codeflix.application.genre.create.DefaultCreateGenreUseCase;
import com.github.allisson95.codeflix.application.genre.delete.DefaultDeleteGenreUseCase;
import com.github.allisson95.codeflix.application.genre.delete.DeleteGenreUseCase;
import com.github.allisson95.codeflix.application.genre.retrieve.get.DefaultGetGenreByIdUseCase;
import com.github.allisson95.codeflix.application.genre.retrieve.get.GetGenreByIdUseCase;
import com.github.allisson95.codeflix.application.genre.retrieve.list.DefaultListGenreUseCase;
import com.github.allisson95.codeflix.application.genre.retrieve.list.ListGenreUseCase;
import com.github.allisson95.codeflix.application.genre.update.DefaultUpdateGenreUseCase;
import com.github.allisson95.codeflix.application.genre.update.UpdateGenreUseCase;
import com.github.allisson95.codeflix.domain.category.CategoryGateway;
import com.github.allisson95.codeflix.domain.genre.GenreGateway;

@Configuration
public class GenreUseCaseConfig {

    private final GenreGateway genreGateway;
    private final CategoryGateway categoryGateway;

    public GenreUseCaseConfig(final GenreGateway genreGateway, final CategoryGateway categoryGateway) {
        this.genreGateway = Objects.requireNonNull(genreGateway);
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Bean
    public CreateGenreUseCase createGenreUseCase() {
        return new DefaultCreateGenreUseCase(this.genreGateway, this.categoryGateway);
    }

    @Bean
    public DeleteGenreUseCase deleteGenreUseCase() {
        return new DefaultDeleteGenreUseCase(this.genreGateway);
    }

    @Bean
    public GetGenreByIdUseCase getGenreByIdUseCase() {
        return new DefaultGetGenreByIdUseCase(this.genreGateway);
    }

    @Bean
    public ListGenreUseCase listGenreUseCase() {
        return new DefaultListGenreUseCase(this.genreGateway);
    }

    @Bean
    public UpdateGenreUseCase updateGenreUseCase() {
        return new DefaultUpdateGenreUseCase(this.genreGateway, this.categoryGateway);
    }

}
