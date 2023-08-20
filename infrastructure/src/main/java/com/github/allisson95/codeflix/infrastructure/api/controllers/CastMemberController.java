package com.github.allisson95.codeflix.infrastructure.api.controllers;

import static org.springframework.web.util.UriComponentsBuilder.fromPath;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.github.allisson95.codeflix.application.castmember.create.CreateCastMemberCommand;
import com.github.allisson95.codeflix.application.castmember.create.CreateCastMemberUseCase;
import com.github.allisson95.codeflix.application.castmember.retrieve.get.GetCastMemberByIdUseCase;
import com.github.allisson95.codeflix.infrastructure.api.CastMemberAPI;
import com.github.allisson95.codeflix.infrastructure.castmember.models.CastMemberResponse;
import com.github.allisson95.codeflix.infrastructure.castmember.models.CreateCastMemberRequest;
import com.github.allisson95.codeflix.infrastructure.castmember.presenters.CastMemberApiPresenter;

@RestController
public class CastMemberController implements CastMemberAPI {

    private final CreateCastMemberUseCase createCastMemberUseCase;
    private final GetCastMemberByIdUseCase getCastMemberByIdUseCase;

    public CastMemberController(
            final CreateCastMemberUseCase createCastMemberUseCase,
            final GetCastMemberByIdUseCase getCastMemberByIdUseCase) {
        this.createCastMemberUseCase = Objects.requireNonNull(createCastMemberUseCase);
        this.getCastMemberByIdUseCase = Objects.requireNonNull(getCastMemberByIdUseCase);
    }

    @Override
    public ResponseEntity<?> create(final CreateCastMemberRequest input) {
        final var aCommand = CreateCastMemberCommand.with(input.name(), input.type());

        final var output = this.createCastMemberUseCase.execute(aCommand);

        final var location = fromPath("/cast_members/{castMembersID}").build(output.id());

        return ResponseEntity.created(location).body(output);
    }

    @Override
    public CastMemberResponse getById(final String castMemberId) {
        return CastMemberApiPresenter.present(this.getCastMemberByIdUseCase.execute(castMemberId));
    }

}
