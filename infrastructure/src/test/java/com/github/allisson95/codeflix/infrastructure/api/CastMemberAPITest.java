package com.github.allisson95.codeflix.infrastructure.api;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
import com.github.allisson95.codeflix.application.castmember.create.CreateCastMemberUseCase;
import com.github.allisson95.codeflix.application.castmember.delete.DeleteCastMemberUseCase;
import com.github.allisson95.codeflix.application.castmember.retrieve.get.GetCastMemberByIdUseCase;
import com.github.allisson95.codeflix.application.castmember.retrieve.list.ListCastMembersUseCase;
import com.github.allisson95.codeflix.application.castmember.update.UpdateCastMemberUseCase;
import com.github.allisson95.codeflix.domain.castmember.CastMemberID;
import com.github.allisson95.codeflix.domain.exceptions.NotificationException;
import com.github.allisson95.codeflix.domain.validation.Error;

@ControllerTest(controllers = CastMemberAPI.class)
class CastMemberAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateCastMemberUseCase createCastMemberUseCase;

    @MockBean
    private DeleteCastMemberUseCase deleteCastMemberUseCase;

    @MockBean
    private GetCastMemberByIdUseCase getCastMemberByIdUseCase;

    @MockBean
    private ListCastMembersUseCase listCastMembersUseCase;

    @MockBean
    private UpdateCastMemberUseCase updateCastMemberUseCase;

    @Test
    void Given_AValidCommand_When_CallsCreateCastMember_Should_ReturnItsIdentifier() throws Exception {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();
        final var expectedId = CastMemberID.unique();

        final var aCommand = new CreateCastMemberRequest(expectedName, expectedType);

        when(createCastMemberUseCase.execute(any()))
                .thenReturn(CreateCastMemberOutput.with(expectedId));

        final var request = post("cast_members")
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

        final var request = post("cast_members")
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

}
