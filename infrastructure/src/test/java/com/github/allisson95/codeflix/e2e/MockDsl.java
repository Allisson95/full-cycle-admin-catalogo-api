package com.github.allisson95.codeflix.e2e;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.github.allisson95.codeflix.domain.Identifier;
import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.domain.genre.GenreID;
import com.github.allisson95.codeflix.infrastructure.category.models.CategoryResponse;
import com.github.allisson95.codeflix.infrastructure.category.models.CreateCategoryRequest;
import com.github.allisson95.codeflix.infrastructure.category.models.UpdateCategoryRequest;
import com.github.allisson95.codeflix.infrastructure.configuration.json.Json;
import com.github.allisson95.codeflix.infrastructure.genre.models.CreateGenreRequest;
import com.github.allisson95.codeflix.infrastructure.genre.models.GenreResponse;
import com.github.allisson95.codeflix.infrastructure.genre.models.UpdateGenreRequest;

public interface MockDsl {

    MockMvc mvc();

    default ResultActions deleteACategory(final CategoryID categoryID) throws Exception {
        return this.delete("/categories/{categoryID}", categoryID);
    }

    default CategoryID givenACategory(final String name, final String description, final boolean isActive) throws Exception {
        final var aRequestBody = new CreateCategoryRequest(name, description, isActive);
        final var categoryId = this.given("/categories", aRequestBody);
        return CategoryID.from(categoryId);
    }

    default ResultActions listCategories(final int page, final int perPage, final String search, final String sort, final String dir) throws Exception {
        return this.list("/categories", page, perPage, search, sort, dir);
    }

    default ResultActions listCategories(final int page, final int perPage, final String search) throws Exception {
        return this.listCategories(page, perPage, search, "", "");
    }

    default ResultActions listCategories(final int page, final int perPage) throws Exception {
        return this.listCategories(page, perPage, "", "", "");
    }

    default CategoryResponse retrieveCategory(final CategoryID categoryID) throws Exception {
        return this.retrieve("/categories/{categoryID}", categoryID, CategoryResponse.class);
    }

    default ResultActions updateCategory(final CategoryID categoryID, final UpdateCategoryRequest aRequestBody) throws Exception {
        return this.update("/categories/{categoryID}", categoryID, aRequestBody);
    }

    default GenreID givenAGenre(final String name, final List<CategoryID> categories, final boolean isActive) throws Exception {
        final var aRequestBody = new CreateGenreRequest(name, mapTo(categories, CategoryID::getValue), isActive);
        final var genreId = this.given("/genres", aRequestBody);
        return GenreID.from(genreId);
    }

    default ResultActions listGenres(final int page, final int perPage, final String search, final String sort, final String dir) throws Exception {
        return this.list("/genres", page, perPage, search, sort, dir);
    }

    default ResultActions listGenres(final int page, final int perPage, final String search) throws Exception {
        return this.listGenres(page, perPage, search, "", "");
    }

    default ResultActions listGenres(final int page, final int perPage) throws Exception {
        return this.listGenres(page, perPage, "", "", "");
    }

    default GenreResponse retrieveGenre(final GenreID genreID) throws Exception {
        return this.retrieve("/genres/{genreID}", genreID, GenreResponse.class);
    }

    default ResultActions updateGenre(final GenreID genreID, final UpdateGenreRequest aRequestBody) throws Exception {
        return this.update("/genres/{genreID}", genreID, aRequestBody);
    }

    default <A, D> List<D> mapTo(final List<A> values, final Function<A, D> mapper) {
        return values.stream()
                .map(mapper)
                .toList();
    }

    private ResultActions delete(final String url, final Identifier anId) throws Exception {
        final var aRequest = MockMvcRequestBuilders.delete(url, anId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        return this.mvc().perform(aRequest)
                .andDo(print());
    }

    private String given(final String url, final Object aRequestBody) throws Exception {
        final var aRequest = post(url)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(aRequestBody));

        final var actualId = this.mvc().perform(aRequest)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getHeader("Location").replace("%s/".formatted(url), "");

        return actualId;
    }

    private ResultActions list(final String url, final int page, final int perPage, final String search, final String sort, final String dir) throws Exception {
        final var aRequest = get(url)
                .param("search", search)
                .param("page", String.valueOf(page))
                .param("perPage", String.valueOf(perPage))
                .param("sort", sort)
                .param("dir", dir)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        return this.mvc().perform(aRequest);
    }

    private <T> T retrieve(final String url, final Identifier anId, final Class<T> clazz) throws Exception {
        final var aRequest = get(url, anId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        final var json = this.mvc().perform(aRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        return Json.readValue(json, clazz);
    }

    private ResultActions update(final String url, final Identifier anId, final Object aRequestBody) throws Exception {
        final var aRequest = put(url, anId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(aRequestBody));

        return this.mvc().perform(aRequest)
                .andDo(print());
    }

}
