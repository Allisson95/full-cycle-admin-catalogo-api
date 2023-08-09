package com.github.allisson95.codeflix.infrastructure.api.controllers;

import static org.springframework.web.util.UriComponentsBuilder.fromPath;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.github.allisson95.codeflix.application.castmember.create.CreateCastMemberCommand;
import com.github.allisson95.codeflix.application.castmember.create.CreateCastMemberUseCase;
import com.github.allisson95.codeflix.infrastructure.api.CastMemberAPI;
import com.github.allisson95.codeflix.infrastructure.castmember.models.CreateCastMemberRequest;

@RestController
public class CastMemberController implements CastMemberAPI {

    private final CreateCastMemberUseCase createCastMemberUseCase;

    public CastMemberController(final CreateCastMemberUseCase createCastMemberUseCase) {
        this.createCastMemberUseCase = Objects.requireNonNull(createCastMemberUseCase);
    }

    @Override
    public ResponseEntity<?> create(final CreateCastMemberRequest input) {
        final var aCommand = CreateCastMemberCommand.with(input.name(), input.type());

        final var output = this.createCastMemberUseCase.execute(aCommand);

        final var location = fromPath("/cast_members/{castMembersID}").build(output.id());

        return ResponseEntity.created(location).body(output);
    }

}
