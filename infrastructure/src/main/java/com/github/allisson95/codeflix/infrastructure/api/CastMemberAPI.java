package com.github.allisson95.codeflix.infrastructure.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.github.allisson95.codeflix.domain.pagination.Pagination;
import com.github.allisson95.codeflix.infrastructure.castmember.models.CastMemberListResponse;
import com.github.allisson95.codeflix.infrastructure.castmember.models.CastMemberResponse;
import com.github.allisson95.codeflix.infrastructure.castmember.models.CreateCastMemberRequest;
import com.github.allisson95.codeflix.infrastructure.castmember.models.UpdateCastMemberRequest;

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

    @GetMapping
    @Operation(summary = "List all cast members paginated")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listed successfuly"),
        @ApiResponse(responseCode = "422", description = "An invalid parameter was received"),
        @ApiResponse(responseCode = "500", description = "An internal server error"),
    })
    Pagination<CastMemberListResponse> listCastMembers(
        @RequestParam(name = "search", required = false, defaultValue = "") final String search,
        @RequestParam(name = "page", required = false, defaultValue = "0") final int page,
        @RequestParam(name = "perPage", required = false, defaultValue = "10") final int perPage,
        @RequestParam(name = "sort", required = false, defaultValue = "name") final String sort,
        @RequestParam(name = "dir", required = false, defaultValue = "asc") final String dir);

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

    @PutMapping(
        value = "{castMemberId}",
        produces = { MediaType.APPLICATION_JSON_VALUE },
        consumes = { MediaType.APPLICATION_JSON_VALUE }
    )
    @Operation(summary = "Update a cast member by it's identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cast member retrieved"),
        @ApiResponse(responseCode = "404", description = "Cast member was not found"),
        @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
        @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    ResponseEntity<?> updateById(@PathVariable(name = "castMemberId") String castMemberId, @RequestBody UpdateCastMemberRequest input);

    @DeleteMapping(value = "{castMemberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a cast member by it's identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cast member deleted"),
        @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    void deleteById(@PathVariable(name = "castMemberId") String castMemberId);

}
