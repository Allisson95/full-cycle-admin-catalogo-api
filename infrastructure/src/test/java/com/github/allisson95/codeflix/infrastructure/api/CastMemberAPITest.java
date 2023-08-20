package com.github.allisson95.codeflix.infrastructure.api;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.github.allisson95.codeflix.Fixture;
import com.github.allisson95.codeflix.application.castmember.create.CreateCastMemberOutput;
import com.github.allisson95.codeflix.application.castmember.create.DefaultCreateCastMemberUseCase;
import com.github.allisson95.codeflix.application.castmember.delete.DefaultDeleteCastMemberUseCase;
import com.github.allisson95.codeflix.application.castmember.retrieve.get.CastMemberOutput;
import com.github.allisson95.codeflix.application.castmember.retrieve.get.DefaultGetCastMemberByIdUseCase;
import com.github.allisson95.codeflix.application.castmember.retrieve.list.DefaultListCastMembersUseCase;
import com.github.allisson95.codeflix.application.castmember.update.DefaultUpdateCastMemberUseCase;
import com.github.allisson95.codeflix.domain.castmember.CastMember;
import com.github.allisson95.codeflix.domain.castmember.CastMemberID;
import com.github.allisson95.codeflix.domain.exceptions.NotFoundException;
import com.github.allisson95.codeflix.domain.exceptions.NotificationException;
import com.github.allisson95.codeflix.domain.validation.Error;
import com.github.allisson95.codeflix.infrastructure.castmember.models.CreateCastMemberRequest;

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
        final var expectedType = Fixture.CastMember.type();
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
        final var expectedType = Fixture.CastMember.type();
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
        final var expectedType = Fixture.CastMember.type();

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

}
