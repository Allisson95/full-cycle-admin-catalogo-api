package com.github.allisson95.codeflix.infrastructure.api;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
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
import com.github.allisson95.codeflix.domain.Fixture;
import com.github.allisson95.codeflix.application.castmember.create.CreateCastMemberOutput;
import com.github.allisson95.codeflix.application.castmember.create.DefaultCreateCastMemberUseCase;
import com.github.allisson95.codeflix.application.castmember.delete.DefaultDeleteCastMemberUseCase;
import com.github.allisson95.codeflix.application.castmember.retrieve.get.CastMemberOutput;
import com.github.allisson95.codeflix.application.castmember.retrieve.get.DefaultGetCastMemberByIdUseCase;
import com.github.allisson95.codeflix.application.castmember.retrieve.list.CastMemberListOutput;
import com.github.allisson95.codeflix.application.castmember.retrieve.list.DefaultListCastMembersUseCase;
import com.github.allisson95.codeflix.application.castmember.update.DefaultUpdateCastMemberUseCase;
import com.github.allisson95.codeflix.application.castmember.update.UpdateCastMemberOutput;
import com.github.allisson95.codeflix.domain.castmember.CastMember;
import com.github.allisson95.codeflix.domain.castmember.CastMemberID;
import com.github.allisson95.codeflix.domain.castmember.CastMemberType;
import com.github.allisson95.codeflix.domain.exceptions.NotFoundException;
import com.github.allisson95.codeflix.domain.exceptions.NotificationException;
import com.github.allisson95.codeflix.domain.pagination.Pagination;
import com.github.allisson95.codeflix.domain.validation.Error;
import com.github.allisson95.codeflix.infrastructure.castmember.models.CreateCastMemberRequest;
import com.github.allisson95.codeflix.infrastructure.castmember.models.UpdateCastMemberRequest;

@ControllerTest(controllers = CastMemberAPI.class)
class CastMemberAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private DefaultCreateCastMemberUseCase createCastMemberUseCase;

    @MockBean
    private DefaultDeleteCastMemberUseCase deleteCastMemberUseCase;

    @MockBean
    private DefaultGetCastMemberByIdUseCase getCastMemberByIdUseCase;

    @MockBean
    private DefaultListCastMembersUseCase listCastMembersUseCase;

    @MockBean
    private DefaultUpdateCastMemberUseCase updateCastMemberUseCase;

    @Test
    void Given_AValidCommand_When_CallsCreateCastMember_Should_ReturnItsIdentifier() throws Exception {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();
        final var expectedId = CastMemberID.unique();

        final var aCommand = new CreateCastMemberRequest(expectedName, expectedType);

        when(createCastMemberUseCase.execute(any()))
                .thenReturn(CreateCastMemberOutput.with(expectedId));

        final var request = post("/cast_members")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(aCommand));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "/cast_members/" + expectedId.getValue()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())));

        verify(createCastMemberUseCase).execute(
                argThat(cmd -> Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedType, cmd.type())));
    }

    @Test
    void Given_AInvalidCommand_When_CallsCreateCastMember_Should_ReturnNotificationException() throws Exception {
        final String expectedName = null;
        final var expectedType = Fixture.CastMembers.type();
        final var expectedErrorMessage = "'name' should not be null";

        final var aCommand = new CreateCastMemberRequest(expectedName, expectedType);

        when(createCastMemberUseCase.execute(any()))
                .thenThrow(NotificationException.with(new Error(expectedErrorMessage)));

        final var request = post("/cast_members")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(aCommand));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().doesNotExist("Location"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(createCastMemberUseCase).execute(
                argThat(cmd -> Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedType, cmd.type())));
    }

    @Test
    void Given_AValidId_When_CallsGetById_Should_ReturnIt() throws Exception {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();

        final var aMember = CastMember.newMember(expectedName, expectedType);
        final var expectedId = aMember.getId();
        final var expectedMember = CastMemberOutput.with(aMember);

        when(getCastMemberByIdUseCase.execute(any()))
                .thenReturn(expectedMember);

        final var request = get("/cast_members/{id}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())))
                .andExpect(jsonPath("$.name", equalTo(expectedMember.name())))
                .andExpect(jsonPath("$.type", equalTo(expectedMember.type().name())))
                .andExpect(jsonPath("$.created_at", equalTo(expectedMember.createdAt().toString())))
                .andExpect(jsonPath("$.updated_at", equalTo(expectedMember.updatedAt().toString())));

        verify(getCastMemberByIdUseCase).execute(expectedId.getValue());
    }

    @Test
    void Given_AInvalidId_When_CallsGetById_Should_ReturnNotificationException() throws Exception {
        final var expectedId = CastMemberID.from("123");
        final var expectedErrorMessage = "CastMember with id 123 was not found";

        when(getCastMemberByIdUseCase.execute(any()))
                .thenThrow(
                        NotFoundException.with(CastMember.class, expectedId));

        final var request = get("/cast_members/{id}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(getCastMemberByIdUseCase).execute(expectedId.getValue());
    }

    @Test
    void Given_AValidCommand_When_CallsUpdateCastMember_Should_ReturnItsIdentifier() throws Exception {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();

        final var actualMember = CastMember.newMember("Vin Dis", CastMemberType.DIRECTOR);
        final var expectedId = actualMember.getId();

        final var aCommand = new UpdateCastMemberRequest(expectedName, expectedType);

        when(updateCastMemberUseCase.execute(any()))
                .thenReturn(UpdateCastMemberOutput.with(expectedId));

        final var request = put("/cast_members/{castMemberId}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(aCommand));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())));

        verify(updateCastMemberUseCase).execute(
                argThat(cmd -> Objects.equals(expectedId.getValue(), cmd.id())
                        && Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedType, cmd.type())));
    }

    @Test
    void Given_AInvalidCommand_When_CallsUpdateCastMember_Should_ReturnNotificationException() throws Exception {
        final String expectedName = null;
        final var expectedType = Fixture.CastMembers.type();

        final var actualMember = CastMember.newMember("Vin Dis", CastMemberType.DIRECTOR);
        final var expectedId = actualMember.getId();

        final var expectedErrorMessage = "'name' should not be null";

        final var aCommand = new UpdateCastMemberRequest(expectedName, expectedType);

        when(updateCastMemberUseCase.execute(any()))
                .thenThrow(NotificationException.with(new Error(expectedErrorMessage)));

        final var request = put("/cast_members/{castMemberId}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(aCommand));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(updateCastMemberUseCase).execute(
                argThat(cmd -> Objects.equals(expectedId.getValue(), cmd.id())
                        && Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedType, cmd.type())));
    }

    @Test
    void Given_AInvalidId_When_CallsUpdateCastMember_Should_ReturnNotFound() throws Exception {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMembers.type();
        final var expectedId = CastMemberID.from("123");
        final var expectedErrorMessage = "CastMember with id 123 was not found";

        final var aCommand = new UpdateCastMemberRequest(expectedName, expectedType);

        when(updateCastMemberUseCase.execute(any()))
                .thenThrow(
                        NotFoundException.with(CastMember.class, expectedId));

        final var request = put("/cast_members/{castMemberId}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(aCommand));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isEmpty());

        verify(updateCastMemberUseCase).execute(
                argThat(cmd -> Objects.equals(expectedId.getValue(), cmd.id())
                        && Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedType, cmd.type())));
    }

    @Test
    void Given_AValidId_When_CallsDeleteById_Should_ReturnNoContent() throws Exception {
        final var expectedId = CastMemberID.unique();

        doNothing().when(deleteCastMemberUseCase).execute(any());

        final var request = delete("/cast_members/{id}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(deleteCastMemberUseCase).execute(expectedId.getValue());
    }

    @Test
    void Given_ValidParams_When_CallListCastMembers_Should_ReturnIt() throws Exception {
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());

        final var expectedPage = 1;
        final var expectedPerPage = 20;
        final var expectedTerms = "Alg";
        final var expectedSort = "type";
        final var expectedDirection = "desc";

        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        final var expectedItems = List.of(CastMemberListOutput.from(aMember));

        when(listCastMembersUseCase.execute(any()))
                .thenReturn(new Pagination<>(
                        expectedPage,
                        expectedPerPage,
                        expectedTotal,
                        expectedItems));

        final var request = get("/cast_members")
                .param("search", expectedTerms)
                .param("page", String.valueOf(expectedPage))
                .param("perPage", String.valueOf(expectedPerPage))
                .param("sort", expectedSort)
                .param("dir", expectedDirection)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                .andExpect(jsonPath("$.items[0].id", equalTo(aMember.getId().getValue())))
                .andExpect(jsonPath("$.items[0].name", equalTo(aMember.getName())))
                .andExpect(jsonPath("$.items[0].type", equalTo(aMember.getType().name())))
                .andExpect(jsonPath("$.items[0].created_at", equalTo(aMember.getCreatedAt().toString())));

        verify(listCastMembersUseCase, times(1)).execute(argThat(cmd -> 
            Objects.equals(expectedPage, cmd.page())
                && Objects.equals(expectedPerPage, cmd.perPage())
                && Objects.equals(expectedTerms, cmd.terms())
                && Objects.equals(expectedSort, cmd.sort())
                && Objects.equals(expectedDirection, cmd.direction())));
    }

    @Test
    void Given_EmptyParams_When_CallListCastMembers_Should_UseDefaultsAndReturnIt() throws Exception {
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMembers.type());

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        final var expectedItems = List.of(CastMemberListOutput.from(aMember));

        when(listCastMembersUseCase.execute(any()))
                .thenReturn(new Pagination<>(
                        expectedPage,
                        expectedPerPage,
                        expectedTotal,
                        expectedItems));

        final var request = get("/cast_members")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                .andExpect(jsonPath("$.items[0].id", equalTo(aMember.getId().getValue())))
                .andExpect(jsonPath("$.items[0].name", equalTo(aMember.getName())))
                .andExpect(jsonPath("$.items[0].type", equalTo(aMember.getType().name())))
                .andExpect(jsonPath("$.items[0].created_at", equalTo(aMember.getCreatedAt().toString())));

        verify(listCastMembersUseCase, times(1)).execute(argThat(cmd -> 
            Objects.equals(expectedPage, cmd.page())
                && Objects.equals(expectedPerPage, cmd.perPage())
                && Objects.equals(expectedTerms, cmd.terms())
                && Objects.equals(expectedSort, cmd.sort())
                && Objects.equals(expectedDirection, cmd.direction())));
    }

}
