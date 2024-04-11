package com.github.allisson95.codeflix.application.video.update;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.github.allisson95.codeflix.domain.Identifier;
import com.github.allisson95.codeflix.domain.castmember.CastMemberGateway;
import com.github.allisson95.codeflix.domain.castmember.CastMemberID;
import com.github.allisson95.codeflix.domain.category.CategoryGateway;
import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.domain.exceptions.DomainException;
import com.github.allisson95.codeflix.domain.exceptions.InternalErrorException;
import com.github.allisson95.codeflix.domain.exceptions.NotFoundException;
import com.github.allisson95.codeflix.domain.exceptions.NotificationException;
import com.github.allisson95.codeflix.domain.genre.GenreGateway;
import com.github.allisson95.codeflix.domain.genre.GenreID;
import com.github.allisson95.codeflix.domain.validation.Error;
import com.github.allisson95.codeflix.domain.validation.ValidationHandler;
import com.github.allisson95.codeflix.domain.validation.handler.Notification;
import com.github.allisson95.codeflix.domain.video.MediaResourceGateway;
import com.github.allisson95.codeflix.domain.video.Rating;
import com.github.allisson95.codeflix.domain.video.Video;
import com.github.allisson95.codeflix.domain.video.VideoGateway;
import com.github.allisson95.codeflix.domain.video.VideoID;
import com.github.allisson95.codeflix.domain.video.VideoMediaType;
import com.github.allisson95.codeflix.domain.video.VideoResource;

public class DefaultUpdateVideoUseCase extends UpdateVideoUseCase {

    private final VideoGateway videoGateway;
    private final MediaResourceGateway mediaResourceGateway;
    private final CategoryGateway categoryGateway;
    private final CastMemberGateway castMemberGateway;
    private final GenreGateway genreGateway;

    public DefaultUpdateVideoUseCase(
            final VideoGateway videoGateway,
            final MediaResourceGateway mediaResourceGateway,
            final CategoryGateway categoryGateway,
            final CastMemberGateway castMemberGateway,
            final GenreGateway genreGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
        this.genreGateway = Objects.requireNonNull(genreGateway);
    }

    @Override
    public UpdateVideoOutput execute(final UpdateVideoCommand aCommand) {
        final var anId = VideoID.from(aCommand.id());

        final var aLauncedAt = aCommand.getLaunchedAt().map(Year::of).orElse(null);
        final var aRating = Rating.of(aCommand.rating()).orElse(null);
        final var categories = toIdentifier(aCommand.categories(), CategoryID::from);
        final var castMembers = toIdentifier(aCommand.castMembers(), CastMemberID::from);
        final var genres = toIdentifier(aCommand.genres(), GenreID::from);

        final var aVideo = this.videoGateway.findById(anId)
                .orElseThrow(notFound(anId));

        final var notification = Notification.create();

        notification.append(validateCategories(categories));
        notification.append(validateCastMembers(castMembers));
        notification.append(validateGenres(genres));

        aVideo.update(
                aCommand.title(),
                aCommand.description(),
                aLauncedAt,
                aCommand.duration(),
                aRating,
                aCommand.opened(),
                aCommand.published(),
                categories,
                genres,
                castMembers);

        aVideo.validate(notification);

        if (notification.hasError()) {
            throw new NotificationException("Could not update Aggregate Video", notification);
        }

        return UpdateVideoOutput.from(update(aCommand, aVideo));
    }

    private Video update(final UpdateVideoCommand aCommand, final Video aVideo) {
        final var anId = aVideo.getId();

        try {
            final var aBannerMedia = aCommand.getBanner()
                    .map(it -> this.mediaResourceGateway.storeImage(anId, VideoResource.with(it, VideoMediaType.BANNER)))
                    .orElse(null);

            final var aThumbnailMedia = aCommand.getThumbnail()
                    .map(it -> this.mediaResourceGateway.storeImage(anId, VideoResource.with(it, VideoMediaType.THUMBNAIL)))
                    .orElse(null);

            final var aThumbnailHalfMedia = aCommand.getThumbnailHalf()
                    .map(it -> this.mediaResourceGateway.storeImage(anId, VideoResource.with(it, VideoMediaType.THUMBNAIL_HALF)))
                    .orElse(null);

            final var aVideoMedia = aCommand.getVideo()
                    .map(it -> this.mediaResourceGateway.storeVideo(anId, VideoResource.with(it, VideoMediaType.VIDEO)))
                    .orElse(null);

            final var aTrailerMedia = aCommand.getTrailer()
                    .map(it -> this.mediaResourceGateway.storeVideo(anId, VideoResource.with(it, VideoMediaType.TRAILER)))
                    .orElse(null);

            aVideo
                    .setBanner(aBannerMedia)
                    .setThumbnail(aThumbnailMedia)
                    .setThumbnailHalf(aThumbnailHalfMedia)
                    .setVideo(aVideoMedia)
                    .setTrailer(aTrailerMedia);

            return this.videoGateway.update(aVideo);
        } catch (final Exception e) {
            throw new InternalErrorException(
                    "An error on update video was observed [videoId:%s]".formatted(anId.getValue()),
                    e);
        }
    }

    private Supplier<? extends DomainException> notFound(final Identifier anId) {
        return () -> NotFoundException.with(Video.class, anId);
    }

    private <T> Set<T> toIdentifier(final Set<String> ids, final Function<String, T> mapper) {
        return ids.stream()
                .map(mapper)
                .collect(Collectors.toSet());
    }

    private ValidationHandler validateCategories(final Set<CategoryID> ids) {
        return validateAggregate("categories", ids, categoryGateway::existsByIds);
    }

    private ValidationHandler validateCastMembers(final Set<CastMemberID> ids) {
        return validateAggregate("cast members", ids, castMemberGateway::existsByIds);
    }

    private ValidationHandler validateGenres(final Set<GenreID> ids) {
        return validateAggregate("genres", ids, genreGateway::existsByIds);
    }

    private <T extends Identifier> ValidationHandler validateAggregate(
            final String aggregate,
            final Set<T> ids,
            final Function<Iterable<T>, List<T>> existsByIds) {
        final var notification = Notification.create();

        if (ids == null || ids.isEmpty()) {
            return notification;
        }

        final var retrievedIds = existsByIds.apply(ids);
        if (ids.size() != retrievedIds.size()) {
            final var commandIds = new ArrayList<>(ids);
            commandIds.removeAll(retrievedIds);

            final var missingIds = commandIds.stream()
                    .map(Identifier::getValue)
                    .collect(Collectors.joining(", "));

            notification.append(new Error("Some %s could not be found: %s".formatted(aggregate, missingIds)));
        }

        return notification;
    }

}
