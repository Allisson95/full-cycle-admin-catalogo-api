package com.github.allisson95.codeflix.infrastructure.api.controllers;

import java.net.URI;
import java.util.Objects;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.github.allisson95.codeflix.application.video.create.CreateVideoCommand;
import com.github.allisson95.codeflix.application.video.create.CreateVideoUseCase;
import com.github.allisson95.codeflix.domain.resource.Resource;
import com.github.allisson95.codeflix.infrastructure.api.VideoAPI;
import com.github.allisson95.codeflix.infrastructure.utils.HashingUtils;
import com.github.allisson95.codeflix.infrastructure.video.models.CreateVideoRequest;

@RestController
public class VideoController implements VideoAPI {

    private final CreateVideoUseCase createVideoUseCase;

    public VideoController(final CreateVideoUseCase createVideoUseCase) {
        this.createVideoUseCase = Objects.requireNonNull(createVideoUseCase);
    }

    @Override
    public ResponseEntity<?> createFull(
            final String title,
            final String description,
            final Integer yearLaunched,
            final Double duration,
            final String rating,
            final Boolean opened,
            final Boolean published,
            final Set<String> categoriesId,
            final Set<String> genresId,
            final Set<String> castMembersId,
            final MultipartFile bannerFile,
            final MultipartFile thumbFile,
            final MultipartFile thumbHalfFile,
            final MultipartFile trailerFile,
            final MultipartFile videoFile) {
        final var aCommand = CreateVideoCommand.with(
                title,
                description,
                yearLaunched,
                duration,
                rating,
                opened,
                published,
                categoriesId,
                genresId,
                castMembersId,
                resourceOf(bannerFile),
                resourceOf(thumbFile),
                resourceOf(thumbHalfFile),
                resourceOf(trailerFile),
                resourceOf(videoFile));

        final var output = this.createVideoUseCase.execute(aCommand);

        return ResponseEntity
                .created(URI.create("/videos/" + output.id()))
                .body(output);
    }

    @Override
    public ResponseEntity<?> createPartial(final CreateVideoRequest request) {
        final var aCommand = CreateVideoCommand.with(
                request.title(),
                request.description(),
                request.yearLaunched(),
                request.duration(),
                request.rating(),
                request.opened(),
                request.published(),
                request.categories(),
                request.genres(),
                request.castMembers());

        final var output = this.createVideoUseCase.execute(aCommand);

        return ResponseEntity
                .created(URI.create("/videos/" + output.id()))
                .body(output);
    }

    private Resource resourceOf(final MultipartFile part) {
        if (part == null) {
            return null;
        }

        try {
            return Resource.of(
                    HashingUtils.checksum(part.getBytes()),
                    part.getBytes(),
                    part.getContentType(),
                    part.getOriginalFilename());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

}
