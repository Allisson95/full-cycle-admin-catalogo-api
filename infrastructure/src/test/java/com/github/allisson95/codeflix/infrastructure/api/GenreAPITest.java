package com.github.allisson95.codeflix.infrastructure.api;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.allisson95.codeflix.ControllerTest;
import com.github.allisson95.codeflix.application.genre.create.CreateGenreOutput;
import com.github.allisson95.codeflix.application.genre.create.CreateGenreUseCase;
import com.github.allisson95.codeflix.application.genre.delete.DeleteGenreUseCase;
import com.github.allisson95.codeflix.application.genre.retrieve.get.GenreOutput;
import com.github.allisson95.codeflix.application.genre.retrieve.get.GetGenreByIdUseCase;
import com.github.allisson95.codeflix.application.genre.retrieve.list.GenreListOutput;
import com.github.allisson95.codeflix.application.genre.retrieve.list.ListGenreUseCase;
import com.github.allisson95.codeflix.application.genre.update.UpdateGenreOutput;
import com.github.allisson95.codeflix.application.genre.update.UpdateGenreUseCase;
import com.github.allisson95.codeflix.domain.category.Category;
import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.domain.exceptions.NotFoundException;
import com.github.allisson95.codeflix.domain.exceptions.NotificationException;
import com.github.allisson95.codeflix.domain.genre.Genre;
import com.github.allisson95.codeflix.domain.genre.GenreID;
import com.github.allisson95.codeflix.domain.pagination.Pagination;
import com.github.allisson95.codeflix.domain.validation.Error;
import com.github.allisson95.codeflix.domain.validation.handler.Notification;
import com.github.allisson95.codeflix.infrastructure.genre.models.CreateGenreRequest;
import com.github.allisson95.codeflix.infrastructure.genre.models.UpdateGenreRequest;

@ControllerTest(controllers = { GenreAPI.class })
class GenreAPITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateGenreUseCase createGenreUseCase;

    @MockBean
    private GetGenreByIdUseCase getGenreByIdUseCase;

    @MockBean
    private UpdateGenreUseCase updateGenreUseCase;

    @MockBean
    private DeleteGenreUseCase deleteGenreUseCase;

    @MockBean
    private ListGenreUseCase listGenreUseCase;

    @Test
    void Given_AValidCommand_When_CallCreateGenre_Then_ReturnGenreId() throws Exception {
        final var filmes = Category.newCategory("Filmes", null, true);

        final var expectedId = "123";
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of(filmes.getId());

        final var anInput = new CreateGenreRequest(expectedName, asString(expectedCategories), expectedIsActive);

        when(createGenreUseCase.execute(any()))
                .thenReturn(CreateGenreOutput.from(GenreID.from(expectedId)));

        final var request = post("/genres")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsBytes(anInput));

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "/genres/" + expectedId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.id", equalTo(expectedId)));

        verify(createGenreUseCase, times(1)).execute(
                argThat(cmd -> Objects.equals(expectedName, cmd.name())
                        && Objects.equals(asString(expectedCategories), cmd.categories())
                        && Objects.equals(expectedIsActive, cmd.active())));
    }

    @Test
    void Given_AInvalidCommandWithNullName_When_CallCreateGenre_Then_ReturnDomainException() throws Exception {
        final String expectedName = null;
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;

        final var anInput = new CreateGenreRequest(expectedName, asString(expectedCategories), expectedIsActive);

        when(createGenreUseCase.execute(any()))
                .thenThrow(NotificationException.with(new Error(expectedErrorMessage)));

        final var request = post("/genres")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsBytes(anInput));

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().doesNotExist("Location"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(expectedErrorCount)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(createGenreUseCase, times(1)).execute(
                argThat(cmd -> Objects.equals(expectedName, cmd.name())
                        && Objects.equals(asString(expectedCategories), cmd.categories())
                        && Objects.equals(expectedIsActive, cmd.active())));
    }

    @Test
    void Given_AValidId_WhenCallsGetGenreById_Should_ReturnGenre() throws Exception {
        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId(), series.getId());

        final var aGenre = Genre.newGenre(expectedName, expectedIsActive).addCategories(expectedCategories);
        final var expectedId = aGenre.getId();
        final var expectedGenre = GenreOutput.from(aGenre);

        when(getGenreByIdUseCase.execute(any())).thenReturn(expectedGenre);

        final var request = get("/genres/{genreId}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())))
                .andExpect(jsonPath("$.name", equalTo(expectedGenre.name())))
                .andExpect(jsonPath("$.categories_id", equalTo(expectedGenre.categories())))
                .andExpect(jsonPath("$.is_active", equalTo(expectedGenre.isActive())))
                .andExpect(jsonPath("$.created_at", equalTo(expectedGenre.createdAt().toString())))
                .andExpect(jsonPath("$.updated_at", equalTo(expectedGenre.updatedAt().toString())))
                .andExpect(jsonPath("$.deleted_at", nullValue()));

        verify(getGenreByIdUseCase, times(1)).execute(expectedId.getValue());
    }

    @Test
    void Given_AInvalidId_WhenCallsGetGenreById_Should_ReturnNotFound() throws Exception {
        final var expectedId = GenreID.from("123");
        final var expectedErrorMessage = "Genre with id 123 was not found";

        when(getGenreByIdUseCase.execute(expectedId.getValue()))
                .thenThrow(
                        NotFoundException.with(Genre.class, expectedId));

        final var request = get("/genres/{genreId}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(getGenreByIdUseCase, times(1)).execute(expectedId.getValue());
    }

    @Test
    void Given_AValidCommand_When_CallsUpdateGenre_Should_ReturnGenreId() throws Exception {
        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId(), series.getId());

        final var aGenre = Genre.newGenre(expectedName, expectedIsActive);
        final var expectedId = aGenre.getId();

        final var anInput = new UpdateGenreRequest(expectedName, asString(expectedCategories), expectedIsActive);

        when(updateGenreUseCase.execute(any())).thenReturn(UpdateGenreOutput.from(aGenre));

        final var request = put("/genres/{genreId}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsBytes(anInput));

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())));

        verify(updateGenreUseCase, times(1)).execute(
                argThat(cmd -> Objects.equals(expectedName, cmd.name())
                        && Objects.equals(asString(expectedCategories), cmd.categories())
                        && Objects.equals(expectedIsActive, cmd.isActive())));
    }

    @Test
    void Given_AnInvalidCommandWithNullName_When_CallsUpdateGenre_Should_ReturnDomainException() throws Exception {
        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);

        final String expectedName = null;
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId(), series.getId());
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var aGenre = Genre.newGenre("Ação", expectedIsActive);
        final var expectedId = aGenre.getId();

        final var anInput = new UpdateGenreRequest(expectedName, asString(expectedCategories), expectedIsActive);

        when(updateGenreUseCase.execute(any()))
                .thenThrow(
                        new NotificationException("Error", Notification.create(new Error(expectedErrorMessage))));

        final var request = put("/genres/{genreId}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsBytes(anInput));

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(expectedErrorCount)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(updateGenreUseCase, times(1)).execute(
                argThat(cmd -> Objects.equals(expectedName, cmd.name())
                        && Objects.equals(asString(expectedCategories), cmd.categories())
                        && Objects.equals(expectedIsActive, cmd.isActive())));
    }

    @Test
    void Given_AValidGenreID_When_CallsDeleteGenre_Should_BeOk() throws Exception {
        final var aGenre = Genre.newGenre("Ação", true);
        final var expectedId = aGenre.getId();

        doNothing().when(deleteGenreUseCase).execute(expectedId.getValue());

        final var request = delete("/genres/{genreId}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(deleteGenreUseCase, times(1)).execute(expectedId.getValue());
    }

    @Test
    void Given_AnInvalidGenreID_When_CallsDeleteGenre_Should_BeOk() throws Exception {
        final var expectedId = GenreID.from("invalid");

        doNothing().when(deleteGenreUseCase).execute(expectedId.getValue());

        final var request = delete("/genres/{genreId}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(deleteGenreUseCase, times(1)).execute(expectedId.getValue());
    }

    @Test
    void Given_AValidParams_When_CallsListGenres_Should_ReturnPaginated() throws Exception {
        final var aGenre = Genre.newGenre("Series", false);

        final var expectedTerms = "ri";
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var expectedItemsCount = 1;
        final var expectedTotal = 1;
        final var expectedItems = List.of(GenreListOutput.from(aGenre));

        when(listGenreUseCase.execute(any()))
                .thenReturn(new Pagination<>(
                        expectedPage,
                        expectedPerPage,
                        expectedTotal,
                        expectedItems));

        final var request = get("/genres")
                .queryParam("search", expectedTerms)
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectedPerPage))
                .queryParam("sort", expectedSort)
                .queryParam("dir", expectedDirection)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                .andExpect(jsonPath("$.items[0].id", equalTo(aGenre.getId().getValue())))
                .andExpect(jsonPath("$.items[0].name", equalTo(aGenre.getName())))
                .andExpect(jsonPath("$.items[0].is_active", equalTo(aGenre.isActive())))
                .andExpect(jsonPath("$.items[0].created_at", equalTo(aGenre.getCreatedAt().toString())))
                .andExpect(jsonPath("$.items[0].deleted_at", equalTo(aGenre.getDeletedAt().toString())));

        verify(listGenreUseCase, times(1)).execute(argThat(query -> 
            Objects.equals(query.page(), expectedPage)
                && Objects.equals(query.perPage(), expectedPerPage)
                && Objects.equals(query.terms(), expectedTerms)
                && Objects.equals(query.sort(), expectedSort)
                && Objects.equals(query.direction(), expectedDirection)));
    }

    private List<String> asString(final List<CategoryID> categories) {
        return categories.stream()
                .map(CategoryID::getValue)
                .toList();
    }

}
