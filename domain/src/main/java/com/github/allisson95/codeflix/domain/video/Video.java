package com.github.allisson95.codeflix.domain.video;

import java.time.Instant;
import java.time.Year;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.github.allisson95.codeflix.domain.AggregateRoot;
import com.github.allisson95.codeflix.domain.castmember.CastMemberID;
import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.domain.events.DomainEvent;
import com.github.allisson95.codeflix.domain.genre.GenreID;
import com.github.allisson95.codeflix.domain.utils.InstantUtils;
import com.github.allisson95.codeflix.domain.validation.ValidationHandler;

public class Video extends AggregateRoot<VideoID> {

    private String title;
    private String description;
    private Year launchedAt;
    private double duration;
    private Rating rating;

    private boolean opened;
    private boolean published;

    private Instant createdAt;
    private Instant updatedAt;

    private ImageMedia banner;
    private ImageMedia thumbnail;
    private ImageMedia thumbnailHalf;

    private VideoMedia trailer;
    private VideoMedia video;

    private Set<CategoryID> categories;
    private Set<GenreID> genres;
    private Set<CastMemberID> castMembers;

    private Video(
            final VideoID anId,
            final String aTitle,
            final String aDescription,
            final Year aLaunchedYear,
            final double aDuration,
            final Rating aRating,
            final boolean wasOpened,
            final boolean wasPublished,
            final Instant aCreationDate,
            final Instant aUpdateDate,
            final ImageMedia aBanner,
            final ImageMedia aThumbnail,
            final ImageMedia aThumbnailHalf,
            final VideoMedia aTrailer,
            final VideoMedia aVideo,
            final Set<CategoryID> categories,
            final Set<GenreID> genres,
            final Set<CastMemberID> castMembers,
            final List<DomainEvent> domainEvents) {
        super(anId, domainEvents);
        this.title = aTitle;
        this.description = aDescription;
        this.launchedAt = aLaunchedYear;
        this.duration = aDuration;
        this.rating = aRating;
        this.opened = wasOpened;
        this.published = wasPublished;
        this.createdAt = aCreationDate;
        this.updatedAt = aUpdateDate;
        this.banner = aBanner;
        this.thumbnail = aThumbnail;
        this.thumbnailHalf = aThumbnailHalf;
        this.trailer = aTrailer;
        this.video = aVideo;
        this.categories = categories;
        this.genres = genres;
        this.castMembers = castMembers;
    }

    public static Video newVideo(
            final String aTitle,
            final String aDescription,
            final Year aLaunchedYear,
            final double aDuration,
            final Rating aRating,
            final boolean wasOpened,
            final boolean wasPublished,
            final Set<CategoryID> categories,
            final Set<GenreID> genres,
            final Set<CastMemberID> castMembers) {
        final var anId = VideoID.unique();
        final var now = InstantUtils.now();

        return new Video(
                anId,
                aTitle,
                aDescription,
                aLaunchedYear,
                aDuration,
                aRating,
                wasOpened,
                wasPublished,
                now,
                now,
                null,
                null,
                null,
                null,
                null,
                categories,
                genres,
                castMembers,
                null);
    }

    public static Video with(final Video video) {
        return new Video(
                video.getId(),
                video.getTitle(),
                video.getDescription(),
                video.getLaunchedAt(),
                video.getDuration(),
                video.getRating(),
                video.isOpened(),
                video.isPublished(),
                video.getCreatedAt(),
                video.getUpdatedAt(),
                video.getBanner().orElse(null),
                video.getThumbnail().orElse(null),
                video.getThumbnailHalf().orElse(null),
                video.getTrailer().orElse(null),
                video.getVideo().orElse(null),
                new HashSet<>(video.getCategories()),
                new HashSet<>(video.getGenres()),
                new HashSet<>(video.getCastMembers()),
                video.getDomainEvents());
    }

    public static Video with(
            final VideoID anId,
            final String aTitle,
            final String aDescription,
            final Year aLaunchedYear,
            final double aDuration,
            final Rating aRating,
            final boolean wasOpened,
            final boolean wasPublished,
            final Instant aCreationDate,
            final Instant aUpdateDate,
            final ImageMedia aBanner,
            final ImageMedia aThumbnail,
            final ImageMedia aThumbnailHalf,
            final VideoMedia aTrailer,
            final VideoMedia aVideo,
            final Set<CategoryID> categories,
            final Set<GenreID> genres,
            final Set<CastMemberID> castMembers) {
        return new Video(
                anId,
                aTitle,
                aDescription,
                aLaunchedYear,
                aDuration,
                aRating,
                wasOpened,
                wasPublished,
                aCreationDate,
                aUpdateDate,
                aBanner,
                aThumbnail,
                aThumbnailHalf,
                aTrailer,
                aVideo,
                categories,
                genres,
                castMembers,
                null);
    }

    public Video updateBannerMedia(final ImageMedia aBanner) {
        this.banner = aBanner;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Video updateThumbnailMedia(final ImageMedia aThumbnail) {
        this.thumbnail = aThumbnail;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Video updateThumbnailHalfMedia(final ImageMedia aThumbnailHalf) {
        this.thumbnailHalf = aThumbnailHalf;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Video updateTrailerMedia(final VideoMedia aTrailer) {
        this.trailer = aTrailer;
        this.updatedAt = InstantUtils.now();

        onVideoMediaUpdated(aTrailer);

        return this;
    }

    public Video updateVideoMedia(final VideoMedia aVideo) {
        this.video = aVideo;
        this.updatedAt = InstantUtils.now();

        onVideoMediaUpdated(aVideo);

        return this;
    }

    public Video update(
            final String aTitle,
            final String aDescription,
            final Year aLaunchedYear,
            final double aDuration,
            final Rating aRating,
            final boolean wasOpened,
            final boolean wasPublished,
            final Set<CategoryID> categories,
            final Set<GenreID> genres,
            final Set<CastMemberID> castMembers) {

        this.title = aTitle;
        this.description = aDescription;
        this.launchedAt = aLaunchedYear;
        this.duration = aDuration;
        this.rating = aRating;
        this.opened = wasOpened;
        this.published = wasPublished;

        this.setCategories(categories);
        this.setGenres(genres);
        this.setCastMembers(castMembers);

        this.updatedAt = InstantUtils.now();

        return this;
    }

    public Video processing(final VideoMediaType aType) {
        switch (aType) {
            case TRAILER:
                getTrailer().ifPresent(media -> updateTrailerMedia(media.processing()));
                break;
            case VIDEO:
                getVideo().ifPresent(media -> updateVideoMedia(media.processing()));
                break;

            default:
                break;
        }

        return this;
    }

    public Video completed(final VideoMediaType aType, final String encodedLocation) {
        switch (aType) {
            case TRAILER:
                getTrailer().ifPresent(media -> updateTrailerMedia(media.completed(encodedLocation)));
                break;
            case VIDEO:
                getVideo().ifPresent(media -> updateVideoMedia(media.completed(encodedLocation)));
                break;

            default:
                break;
        }

        return this;
    }

    @Override
    public void validate(final ValidationHandler aHandler) {
        new VideoValidator(this, aHandler).validate();
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Year getLaunchedAt() {
        return launchedAt;
    }

    public double getDuration() {
        return duration;
    }

    public Rating getRating() {
        return rating;
    }

    public boolean isOpened() {
        return opened;
    }

    public boolean isPublished() {
        return published;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Optional<ImageMedia> getBanner() {
        return Optional.ofNullable(banner);
    }

    public Optional<ImageMedia> getThumbnail() {
        return Optional.ofNullable(thumbnail);
    }

    public Optional<ImageMedia> getThumbnailHalf() {
        return Optional.ofNullable(thumbnailHalf);
    }

    public Optional<VideoMedia> getTrailer() {
        return Optional.ofNullable(trailer);
    }

    public Optional<VideoMedia> getVideo() {
        return Optional.ofNullable(video);
    }

    public Set<CategoryID> getCategories() {
        return this.categories != null ? Collections.unmodifiableSet(categories) : Collections.emptySet();
    }

    public Set<GenreID> getGenres() {
        return this.genres != null ? Collections.unmodifiableSet(genres) : Collections.emptySet();
    }

    public Set<CastMemberID> getCastMembers() {
        return this.castMembers != null ? Collections.unmodifiableSet(castMembers) : Collections.emptySet();
    }

    private void setCategories(final Set<CategoryID> categories) {
        this.categories = categories != null ? new HashSet<>(categories) : Collections.emptySet();
    }

    private void setGenres(final Set<GenreID> genres) {
        this.genres = genres != null ? new HashSet<>(genres) : Collections.emptySet();
    }

    private void setCastMembers(final Set<CastMemberID> castMembers) {
        this.castMembers = castMembers != null ? new HashSet<>(castMembers) : Collections.emptySet();
    }

    private void onVideoMediaUpdated(final VideoMedia videoMedia) {
        if (videoMedia != null && videoMedia.isPendingEncode()) {
            this.registerEvent(new VideoMediaCreated(getId().getValue(), videoMedia.rawLocation()));
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

}
