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
import com.github.allisson95.codeflix.infrastructure.category.models.CategoryResponse;
import com.github.allisson95.codeflix.infrastructure.category.models.CategoryListResponse;
import com.github.allisson95.codeflix.infrastructure.category.models.CreateCategoryRequest;
import com.github.allisson95.codeflix.infrastructure.category.models.UpdateCategoryRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping(path = "categories")
@Tag(name = "Category")
public interface CategoryAPI {

    @PostMapping(
        consumes = { MediaType.APPLICATION_JSON_VALUE },
        produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    @Operation(summary = "Create a new category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created successfuly"),
        @ApiResponse(responseCode = "422", description = "A validation error"),
        @ApiResponse(responseCode = "500", description = "An internal server error"),
    })
    ResponseEntity<?> createCategory(@RequestBody CreateCategoryRequest input);

    @GetMapping
    @Operation(summary = "List all categories paginated")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listed successfuly"),
        @ApiResponse(responseCode = "422", description = "An invalid parameter was received"),
        @ApiResponse(responseCode = "500", description = "An internal server error"),
    })
    Pagination<CategoryListResponse> listCategories(
        @RequestParam(name = "search", required = false, defaultValue = "") final String search,
        @RequestParam(name = "page", required = false, defaultValue = "0") final int page,
        @RequestParam(name = "perPage", required = false, defaultValue = "10") final int perPage,
        @RequestParam(name = "sort", required = false, defaultValue = "name") final String sort,
        @RequestParam(name = "dir", required = false, defaultValue = "asc") final String dir);

    @GetMapping(
        value = "{categoryId}",
        consumes = { MediaType.APPLICATION_JSON_VALUE },
        produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    @Operation(summary = "Get a category by it's identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category retrieved successfuly"),
        @ApiResponse(responseCode = "404", description = "Category was not found"),
        @ApiResponse(responseCode = "500", description = "An internal server error"),
    })
    CategoryResponse getById(@PathVariable(name = "categoryId") String categoryId);

    @PutMapping(
        value = "{categoryId}",
        consumes = { MediaType.APPLICATION_JSON_VALUE },
        produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    @Operation(summary = "Update a category by it's identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category updated successfuly"),
        @ApiResponse(responseCode = "404", description = "Category was not found"),
        @ApiResponse(responseCode = "422", description = "A validation error"),
        @ApiResponse(responseCode = "500", description = "An internal server error"),
    })
    ResponseEntity<?> updateById(@PathVariable(name = "categoryId") String categoryId, @RequestBody UpdateCategoryRequest input);

    @DeleteMapping(value = "{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a category by it's identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Category retrieved successfuly"),
        @ApiResponse(responseCode = "404", description = "Category was not found"),
        @ApiResponse(responseCode = "500", description = "An internal server error"),
    })
    void deleteById(@PathVariable(name = "categoryId") String categoryId);

}
