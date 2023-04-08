package com.github.allisson95.codeflix.infrastructure.api;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.github.allisson95.codeflix.domain.category.Category;
import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.domain.exceptions.NotificationException;
import com.github.allisson95.codeflix.domain.genre.GenreID;
import com.github.allisson95.codeflix.domain.validation.Error;
import com.github.allisson95.codeflix.infrastructure.genre.models.CreateGenreRequest;

@ControllerTest(controllers = { GenreAPI.class })
class GenreAPITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateGenreUseCase createGenreUseCase;

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

    private List<String> asString(final List<CategoryID> categories) {
        return categories.stream()
                .map(CategoryID::getValue)
                .toList();
    }

}
