package com.github.allisson95.codeflix.infrastructure.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.allisson95.codeflix.infrastructure.castmember.models.CastMemberResponse;
import com.github.allisson95.codeflix.infrastructure.castmember.models.CreateCastMemberRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping(value = "cast_members")
@Tag(name = "Cast Members")
public interface CastMemberAPI {

    @PostMapping(
        consumes = { MediaType.APPLICATION_JSON_VALUE },
        produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    @Operation(summary = "Create a new cast member")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created successfully"),
        @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
        @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    ResponseEntity<?> create(@RequestBody CreateCastMemberRequest input);

    @GetMapping(
        value = "{castMemberId}",
        produces = { MediaType.APPLICATION_JSON_VALUE },
        consumes = { MediaType.APPLICATION_JSON_VALUE }
    )
    @Operation(summary = "Get a cast member by it's identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cast member retrieved"),
        @ApiResponse(responseCode = "404", description = "Cast member was not found"),
        @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    CastMemberResponse getById(@PathVariable(name = "castMemberId") String castMemberId);

}
