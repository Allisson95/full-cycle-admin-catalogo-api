package com.github.allisson95.codeflix.infrastructure.api;

import static io.vavr.API.Left;
import static io.vavr.API.Right;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.allisson95.codeflix.ControllerTest;
import com.github.allisson95.codeflix.application.category.create.CreateCategoryOutput;
import com.github.allisson95.codeflix.application.category.create.CreateCategoryUseCase;
import com.github.allisson95.codeflix.application.category.delete.DeleteCategoryUseCase;
import com.github.allisson95.codeflix.application.category.retrieve.get.CategoryOutput;
import com.github.allisson95.codeflix.application.category.retrieve.get.GetCategoryByIdUseCase;
import com.github.allisson95.codeflix.application.category.update.UpdateCategoryOutput;
import com.github.allisson95.codeflix.application.category.update.UpdateCategoryUseCase;
import com.github.allisson95.codeflix.domain.category.Category;
import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.domain.exceptions.DomainException;
import com.github.allisson95.codeflix.domain.exceptions.NotFoundException;
import com.github.allisson95.codeflix.domain.validation.Error;
import com.github.allisson95.codeflix.domain.validation.handler.Notification;
import com.github.allisson95.codeflix.infrastructure.category.models.CreateCategoryApiInput;
import com.github.allisson95.codeflix.infrastructure.category.models.UpdateCategoryApiInput;

@ControllerTest(controllers = { CategoryAPI.class })
class CategoryAPITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateCategoryUseCase createCategoryUseCase;

    @MockBean
    private GetCategoryByIdUseCase getCategoryByIdUseCase;

    @MockBean
    private UpdateCategoryUseCase updateCategoryUseCase;

    @MockBean
    private DeleteCategoryUseCase deleteCategoryUseCase;

    @Test
    void Given_AValidCommand_When_CallCreateCategory_Then_ReturnCategoryId() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        when(createCategoryUseCase.execute(any()))
            .thenReturn(Right(CreateCategoryOutput.from(CategoryID.from("123"))));

        final var anInput = new CreateCategoryApiInput(expectedName, expectedDescription, expectedIsActive);

        final var request = post("/categories")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.mapper.writeValueAsBytes(anInput));

        this.mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(header().string("Location", "/categories/123"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.id").isString())
            .andExpect(jsonPath("$.id", equalTo("123")));

        verify(createCategoryUseCase, times(1)).execute(
            argThat(cmd -> Objects.equals(expectedName, cmd.name())
                && Objects.equals(expectedDescription, cmd.description())
                && Objects.equals(expectedIsActive, cmd.isActive())));
    }

    @Test
    void Given_AnInvalidName_When_CallCreateCategory_Then_ReturnNotification() throws Exception {
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";

        when(createCategoryUseCase.execute(any()))
            .thenReturn(Left(Notification.create(new Error(expectedErrorMessage))));

        final var anInput = new CreateCategoryApiInput(expectedName, expectedDescription, expectedIsActive);

        final var request = post("/categories")
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
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(createCategoryUseCase, times(1)).execute(
            argThat(cmd -> Objects.equals(expectedName, cmd.name())
                && Objects.equals(expectedDescription, cmd.description())
                && Objects.equals(expectedIsActive, cmd.isActive())));
    }

    @Test
    void Given_AValidCommand_When_CallCreateCategoryAndThrowsRandomException_Then_ReturnNotification() throws Exception {
        final String expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "Random domain error";

        doThrow(DomainException.with(new Error(expectedErrorMessage))).when(createCategoryUseCase).execute(any());

        final var anInput = new CreateCategoryApiInput(expectedName, expectedDescription, expectedIsActive);

        final var request = post("/categories")
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
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(createCategoryUseCase, times(1)).execute(
            argThat(cmd -> Objects.equals(expectedName, cmd.name())
                && Objects.equals(expectedDescription, cmd.description())
                && Objects.equals(expectedIsActive, cmd.isActive())));
    }

    @Test
    void Given_AValidId_When_CallsGetCategoryById_Should_ReturnCategory() throws Exception {
        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final var expectedId = aCategory.getId().getValue();
        final var expectedCategory = CategoryOutput.from(aCategory);

        when(getCategoryByIdUseCase.execute(expectedId)).thenReturn(expectedCategory);

        final var request = get("/categories/{categoryId}", expectedId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", equalTo(expectedId)))
            .andExpect(jsonPath("$.name", equalTo(expectedCategory.name())))
            .andExpect(jsonPath("$.description", equalTo(expectedCategory.description())))
            .andExpect(jsonPath("$.is_active", equalTo(expectedCategory.isActive())))
            .andExpect(jsonPath("$.created_at", equalTo(expectedCategory.createdAt().toString())))
            .andExpect(jsonPath("$.updated_at", equalTo(expectedCategory.updatedAt().toString())))
            .andExpect(jsonPath("$.deleted_at", nullValue()));

        verify(getCategoryByIdUseCase, times(1)).execute(expectedId);
    }

    @Test
    void Given_AInvalidId_When_CallsGetCategoryById_Should_ReturnNotFound() throws Exception {
        final var expectedId = CategoryID.from("123");
        final var expectedErrorMessage = "Category with id 123 was not found";

        when(getCategoryByIdUseCase.execute(expectedId.getValue()))
            .thenThrow(
                NotFoundException.with(Category.class, expectedId)
            );

        final var request = get("/categories/{categoryId}", expectedId.getValue())
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

        verify(getCategoryByIdUseCase, times(1)).execute(expectedId.getValue());
    }

    @Test
    void Given_AValidCommand_When_CallUpdateCategory_Then_ReturnCategoryId() throws Exception {
        final var expectedName = "Filme";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var expectedId = CategoryID.unique();

        when(updateCategoryUseCase.execute(any())).thenReturn(Right(UpdateCategoryOutput.from(expectedId)));

        final var anInput = new UpdateCategoryApiInput(expectedName, expectedDescription, expectedIsActive);

        final var request = put("/categories/{categoryId}", expectedId.getValue())
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

        verify(updateCategoryUseCase, times(1)).execute(argThat(cmd -> 
            Objects.equals(expectedName, cmd.name())
                && Objects.equals(expectedDescription, cmd.description())
                && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    void Given_AnInvalidName_When_CallUpdateCategory_Then_ReturnDomainException() throws Exception {
        final var expectedId = CategoryID.unique();
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        when(updateCategoryUseCase.execute(any()))
            .thenReturn(Left(Notification.create(new Error(expectedErrorMessage))));

        final var anInput = new UpdateCategoryApiInput(expectedName, expectedDescription, expectedIsActive);

        final var request = put("/categories/{categoryId}", expectedId.getValue())
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

        verify(updateCategoryUseCase, times(1)).execute(argThat(cmd -> 
            Objects.equals(expectedName, cmd.name())
                && Objects.equals(expectedDescription, cmd.description())
                && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    void Given_ACommandWithInvalidID_When_CallUpdateCategory_Then_ReturnNotFoundException() throws Exception {
        final var expectedId = CategoryID.from("not-found");
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "Category with id not-found was not found";

        when(updateCategoryUseCase.execute(any()))
            .thenThrow(
                NotFoundException.with(Category.class, expectedId)
            );

        final var anInput = new UpdateCategoryApiInput(expectedName, expectedDescription, expectedIsActive);

        final var request = put("/categories/{categoryId}", expectedId.getValue())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.mapper.writeValueAsBytes(anInput));

        this.mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)))
            .andExpect(jsonPath("$.errors").exists())
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors").isEmpty());

        verify(updateCategoryUseCase, times(1)).execute(argThat(cmd -> 
            Objects.equals(expectedName, cmd.name())
                && Objects.equals(expectedDescription, cmd.description())
                && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    void Given_AValidId_When_CallsDeleteCategory_Should_ReturnNoContent() throws Exception {
        final var expectedId = CategoryID.unique().getValue();

        doNothing().when(deleteCategoryUseCase).execute(expectedId);

        final var request = delete("/categories/{categoryId}", expectedId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isNoContent());

        verify(deleteCategoryUseCase, times(1)).execute(expectedId);
    }

}
