package com.github.allisson95.codeflix.infrastructure.api;

import java.util.Set;

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
import org.springframework.web.multipart.MultipartFile;

import com.github.allisson95.codeflix.domain.pagination.Pagination;
import com.github.allisson95.codeflix.infrastructure.video.models.CreateVideoRequest;
import com.github.allisson95.codeflix.infrastructure.video.models.UpdateVideoRequest;
import com.github.allisson95.codeflix.infrastructure.video.models.VideoListResponse;
import com.github.allisson95.codeflix.infrastructure.video.models.VideoResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping(path = "videos")
@Tag(name = "Video")
public interface VideoAPI {

    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(summary = "List all videos paginated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Videos listed"),
            @ApiResponse(responseCode = "422", description = "A query param was invalid"),
            @ApiResponse(responseCode = "500", description = "An internal server error"),
    })
    Pagination<VideoListResponse> list(
            @RequestParam(name = "search", required = false, defaultValue = "") String search,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "perPage", required = false, defaultValue = "25") int perPage,
            @RequestParam(name = "sort", required = false, defaultValue = "title") String sort,
            @RequestParam(name = "dir", required = false, defaultValue = "asc") String direction,
            @RequestParam(name = "cast_members_ids", required = false, defaultValue = "") Set<String> castMembersIds,
            @RequestParam(name = "categories_ids", required = false, defaultValue = "") Set<String> categoriesIds,
            @RequestParam(name = "genres_ids", required = false, defaultValue = "") Set<String> genresIds);

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(summary = "Create a new video with medias")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created successfuly"),
            @ApiResponse(responseCode = "422", description = "A validation error"),
            @ApiResponse(responseCode = "500", description = "An internal server error"),
    })
    ResponseEntity<?> createFull(
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "year_launched", required = false) Integer yearLaunched,
            @RequestParam(name = "duration", required = false) Double duration,
            @RequestParam(name = "rating", required = false) String rating,
            @RequestParam(name = "opened", required = false) Boolean opened,
            @RequestParam(name = "published", required = false) Boolean published,
            @RequestParam(name = "categories_id", required = false) Set<String> categoriesId,
            @RequestParam(name = "genres_id", required = false) Set<String> genresId,
            @RequestParam(name = "cast_members_id", required = false) Set<String> castMembersId,
            @RequestParam(name = "banner_file", required = false) MultipartFile bannerFile,
            @RequestParam(name = "thumb_file", required = false) MultipartFile thumbFile,
            @RequestParam(name = "thumb_half_file", required = false) MultipartFile thumbHalfFile,
            @RequestParam(name = "trailer_file", required = false) MultipartFile trailerFile,
            @RequestParam(name = "video_file", required = false) MultipartFile videoFile);

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(summary = "Create a new video without medias")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created successfuly"),
            @ApiResponse(responseCode = "422", description = "A validation error"),
            @ApiResponse(responseCode = "500", description = "An internal server error"),
    })
    ResponseEntity<?> createPartial(@RequestBody CreateVideoRequest request);

    @GetMapping(path = "{id}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(summary = "Get a video by it's identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video retrieved successfuly"),
            @ApiResponse(responseCode = "404", description = "Video was not found"),
            @ApiResponse(responseCode = "500", description = "An internal server error"),
    })
    VideoResponse getById(@PathVariable(name = "id") String id);

    @PutMapping(path = "{id}", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(summary = "Update a video by it's identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video updated successfuly"),
            @ApiResponse(responseCode = "404", description = "Video was not found"),
            @ApiResponse(responseCode = "422", description = "A validation error"),
            @ApiResponse(responseCode = "500", description = "An internal server error"),
    })
    ResponseEntity<?> updateById(@PathVariable(name = "id") String id, @RequestBody UpdateVideoRequest request);

    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "{id}")
    @Operation(summary = "Delete a video by it's identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Video deleted"),
            @ApiResponse(responseCode = "500", description = "An internal server error"),
    })
    void deleteById(@PathVariable(name = "id") String id);

}
