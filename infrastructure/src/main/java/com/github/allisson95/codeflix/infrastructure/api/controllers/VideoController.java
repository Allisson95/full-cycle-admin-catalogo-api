package com.github.allisson95.codeflix.infrastructure.api.controllers;

import java.net.URI;
import java.util.Objects;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.github.allisson95.codeflix.application.video.create.CreateVideoCommand;
import com.github.allisson95.codeflix.application.video.create.CreateVideoUseCase;
import com.github.allisson95.codeflix.application.video.delete.DeleteVideoUseCase;
import com.github.allisson95.codeflix.application.video.retrieve.get.GetVideoByIdUseCase;
import com.github.allisson95.codeflix.application.video.update.UpdateVideoCommand;
import com.github.allisson95.codeflix.application.video.update.UpdateVideoUseCase;
import com.github.allisson95.codeflix.domain.resource.Resource;
import com.github.allisson95.codeflix.infrastructure.api.VideoAPI;
import com.github.allisson95.codeflix.infrastructure.utils.HashingUtils;
import com.github.allisson95.codeflix.infrastructure.video.models.CreateVideoRequest;
import com.github.allisson95.codeflix.infrastructure.video.models.UpdateVideoRequest;
import com.github.allisson95.codeflix.infrastructure.video.models.VideoResponse;
import com.github.allisson95.codeflix.infrastructure.video.presenters.VideoApiPresenter;

@RestController
public class VideoController implements VideoAPI {

    private final CreateVideoUseCase createVideoUseCase;
    private final GetVideoByIdUseCase getVideoByIdUseCase;
    private final UpdateVideoUseCase updateVideoUseCase;
    private final DeleteVideoUseCase deleteVideoUseCase;

    public VideoController(
            final CreateVideoUseCase createVideoUseCase,
            final GetVideoByIdUseCase getVideoByIdUseCase,
            final UpdateVideoUseCase updateVideoUseCase,
            final DeleteVideoUseCase deleteVideoUseCase) {
        this.createVideoUseCase = Objects.requireNonNull(createVideoUseCase);
        this.getVideoByIdUseCase = Objects.requireNonNull(getVideoByIdUseCase);
        this.updateVideoUseCase = Objects.requireNonNull(updateVideoUseCase);
        this.deleteVideoUseCase = Objects.requireNonNull(deleteVideoUseCase);
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

    @Override
    public VideoResponse getById(final String id) {
        return VideoApiPresenter.present(this.getVideoByIdUseCase.execute(id));
    }

    @Override
    public ResponseEntity<?> updateById(final String id, final UpdateVideoRequest request) {
        final var aCommand = UpdateVideoCommand.with(
                id,
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

        final var output = this.updateVideoUseCase.execute(aCommand);

        return ResponseEntity.ok(output);
    }

    @Override
    public void deleteById(final String id) {
        this.deleteVideoUseCase.execute(id);
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
