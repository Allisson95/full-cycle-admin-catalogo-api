package com.github.allisson95.codeflix.infrastructure.api.controllers;

import static com.github.allisson95.codeflix.domain.utils.CollectionUtils.mapTo;

import java.net.URI;
import java.util.Objects;
import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.github.allisson95.codeflix.application.video.create.CreateVideoCommand;
import com.github.allisson95.codeflix.application.video.create.CreateVideoUseCase;
import com.github.allisson95.codeflix.application.video.delete.DeleteVideoUseCase;
import com.github.allisson95.codeflix.application.video.media.get.GetMediaCommand;
import com.github.allisson95.codeflix.application.video.media.get.GetMediaUseCase;
import com.github.allisson95.codeflix.application.video.media.upload.UploadMediaCommand;
import com.github.allisson95.codeflix.application.video.media.upload.UploadMediaUseCase;
import com.github.allisson95.codeflix.application.video.retrieve.get.GetVideoByIdUseCase;
import com.github.allisson95.codeflix.application.video.retrieve.list.ListVideoUseCase;
import com.github.allisson95.codeflix.application.video.update.UpdateVideoCommand;
import com.github.allisson95.codeflix.application.video.update.UpdateVideoUseCase;
import com.github.allisson95.codeflix.domain.castmember.CastMemberID;
import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.domain.exceptions.NotificationException;
import com.github.allisson95.codeflix.domain.genre.GenreID;
import com.github.allisson95.codeflix.domain.pagination.Pagination;
import com.github.allisson95.codeflix.domain.resource.Resource;
import com.github.allisson95.codeflix.domain.validation.Error;
import com.github.allisson95.codeflix.domain.video.VideoMediaType;
import com.github.allisson95.codeflix.domain.video.VideoResource;
import com.github.allisson95.codeflix.domain.video.VideoSearchQuery;
import com.github.allisson95.codeflix.infrastructure.api.VideoAPI;
import com.github.allisson95.codeflix.infrastructure.utils.HashingUtils;
import com.github.allisson95.codeflix.infrastructure.video.models.CreateVideoRequest;
import com.github.allisson95.codeflix.infrastructure.video.models.UpdateVideoRequest;
import com.github.allisson95.codeflix.infrastructure.video.models.VideoListResponse;
import com.github.allisson95.codeflix.infrastructure.video.models.VideoResponse;
import com.github.allisson95.codeflix.infrastructure.video.presenters.VideoApiPresenter;

@RestController
public class VideoController implements VideoAPI {

    private final CreateVideoUseCase createVideoUseCase;
    private final GetVideoByIdUseCase getVideoByIdUseCase;
    private final UpdateVideoUseCase updateVideoUseCase;
    private final DeleteVideoUseCase deleteVideoUseCase;
    private final ListVideoUseCase listVideoUseCase;
    private final GetMediaUseCase getMediaUseCase;
    private final UploadMediaUseCase uploadMediaUseCase;

    public VideoController(
            final CreateVideoUseCase createVideoUseCase,
            final GetVideoByIdUseCase getVideoByIdUseCase,
            final UpdateVideoUseCase updateVideoUseCase,
            final DeleteVideoUseCase deleteVideoUseCase,
            final ListVideoUseCase listVideoUseCase,
            final GetMediaUseCase getMediaUseCase,
            final UploadMediaUseCase uploadMediaUseCase) {
        this.createVideoUseCase = Objects.requireNonNull(createVideoUseCase);
        this.getVideoByIdUseCase = Objects.requireNonNull(getVideoByIdUseCase);
        this.updateVideoUseCase = Objects.requireNonNull(updateVideoUseCase);
        this.deleteVideoUseCase = Objects.requireNonNull(deleteVideoUseCase);
        this.listVideoUseCase = Objects.requireNonNull(listVideoUseCase);
        this.getMediaUseCase = Objects.requireNonNull(getMediaUseCase);
        this.uploadMediaUseCase = Objects.requireNonNull(uploadMediaUseCase);
    }

    @Override
    public Pagination<VideoListResponse> list(
            final String search,
            final int page,
            final int perPage,
            final String sort,
            final String direction,
            final Set<String> castMembersIds,
            final Set<String> categoriesIds,
            final Set<String> genresIds) {
        final var aQuery = new VideoSearchQuery(
                page,
                perPage,
                search,
                sort,
                direction,
                mapTo(castMembersIds, CastMemberID::from),
                mapTo(categoriesIds, CategoryID::from),
                mapTo(genresIds, GenreID::from));

        return VideoApiPresenter.present(this.listVideoUseCase.execute(aQuery));
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

    @Override
    public ResponseEntity<byte[]> getMediaByType(final String id, final String type) {
        final var aMedia = this.getMediaUseCase.execute(GetMediaCommand.with(id, type));

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(aMedia.contentType()))
                .contentLength(aMedia.content().length)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=%s".formatted(aMedia.name()))
                .body(aMedia.content());
    }

    @Override
    public ResponseEntity<?> uploadMediaByType(final String id, final String type, final MultipartFile file) {
        final var mediaType = VideoMediaType.of(type)
                .orElseThrow(
                        () -> NotificationException.with(new Error("Invalid %s for VideoMediaType".formatted(type))));

        final var command = UploadMediaCommand.with(id, VideoResource.with(resourceOf(file), mediaType));

        final var output = this.uploadMediaUseCase.execute(command);

        return ResponseEntity
                .created(URI.create("/videos/%s/medias/%s".formatted(id, type)))
                .body(VideoApiPresenter.present(output));
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
