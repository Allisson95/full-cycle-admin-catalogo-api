package com.github.allisson95.codeflix.e2e;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.function.Function;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.domain.genre.GenreID;
import com.github.allisson95.codeflix.infrastructure.category.models.CreateCategoryRequest;
import com.github.allisson95.codeflix.infrastructure.configuration.json.Json;
import com.github.allisson95.codeflix.infrastructure.genre.models.CreateGenreRequest;

public interface MockDsl {

    MockMvc mvc();

    default CategoryID givenACategory(final String name, final String description, final boolean isActive)
            throws Exception {
        final var aRequestBody = new CreateCategoryRequest(name, description, isActive);
        final var categoryId = this.given("/categories", aRequestBody);
        return CategoryID.from(categoryId);
    }

    default GenreID givenAGenre(final String name, final List<CategoryID> categories, final boolean isActive)
            throws Exception {
        final var aRequestBody = new CreateGenreRequest(name, mapTo(categories, CategoryID::getValue), isActive);
        final var genreId = this.given("/genres", aRequestBody);
        return GenreID.from(genreId);
    }

    default <A, D> List<D> mapTo(final List<A> values, final Function<A, D> mapper) {
        return values.stream()
                .map(mapper)
                .toList();
    }

    private String given(final String url, final Object body) throws Exception {
        final var aRequest = post(url)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(body));

        final var actualId = this.mvc().perform(aRequest)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getHeader("Location").replace("%s/".formatted(url), "");

        return actualId;
    }

}
