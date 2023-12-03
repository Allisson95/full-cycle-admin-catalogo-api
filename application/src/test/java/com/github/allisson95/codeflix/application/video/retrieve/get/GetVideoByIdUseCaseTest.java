package com.github.allisson95.codeflix.application.video.retrieve.get;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.github.allisson95.codeflix.application.Fixture;
import com.github.allisson95.codeflix.application.UseCaseTest;
import com.github.allisson95.codeflix.domain.castmember.CastMemberID;
import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.domain.exceptions.NotFoundException;
import com.github.allisson95.codeflix.domain.genre.GenreID;
import com.github.allisson95.codeflix.domain.video.ImageMedia;
import com.github.allisson95.codeflix.domain.video.MediaStatus;
import com.github.allisson95.codeflix.domain.video.Resource;
import com.github.allisson95.codeflix.domain.video.Video;
import com.github.allisson95.codeflix.domain.video.VideoGateway;
import com.github.allisson95.codeflix.domain.video.VideoID;
import com.github.allisson95.codeflix.domain.video.VideoMedia;

class GetVideoByIdUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultGetVideoByIdUseCase useCase;

    @Mock
    private VideoGateway videoGateway;

    @Override
    protected List<Object> getMocksForClean() {
        return List.of(videoGateway);
    }

    @Test
    void Given_AValidId_When_CallsGetVideo_Should_ReturnIt() {
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchedAt = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.<CategoryID>of(Fixture.Categories.random().getId());
        final var expectedGenres = Set.<GenreID>of(Fixture.Genres.random().getId());
        final var expectedCastMembers = Set.<CastMemberID>of(
                Fixture.CastMembers.clintEastwood().getId(),
                Fixture.CastMembers.morganFreeman().getId());

        final var expectedBanner = imageMedia(Resource.Type.BANNER);
        final var expectedThumbnail = imageMedia(Resource.Type.THUMBNAIL);
        final var expectedThumbnailHalf = imageMedia(Resource.Type.THUMBNAIL_HALF);
        final var expectedTrailer = videoMedia(Resource.Type.TRAILER);
        final var expectedVideo = videoMedia(Resource.Type.VIDEO);

        final var aVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedRating,
                expectedOpened,
                expectedPublished,
                expectedCategories,
                expectedGenres,
                expectedCastMembers)
                .setBanner(expectedBanner)
                .setThumbnail(expectedThumbnail)
                .setThumbnailHalf(expectedThumbnailHalf)
                .setTrailer(expectedTrailer)
                .setVideo(expectedVideo);

        final var expectedId = aVideo.getId();

        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(Video.with(aVideo)));

        final var actualVideo = this.useCase.execute(expectedId.getValue());

        assertEquals(expectedId.getValue(), actualVideo.id());
        assertEquals(expectedTitle, actualVideo.title());
        assertEquals(expectedDescription, actualVideo.description());
        assertEquals(expectedLaunchedAt.getValue(), actualVideo.launchedAt());
        assertEquals(expectedDuration, actualVideo.duration());
        assertEquals(expectedRating, actualVideo.rating());
        assertEquals(expectedOpened, actualVideo.opened());
        assertEquals(expectedPublished, actualVideo.published());
        assertEquals(asString(expectedCategories), actualVideo.categories());
        assertEquals(asString(expectedGenres), actualVideo.genres());
        assertEquals(asString(expectedCastMembers), actualVideo.castMembers());
        assertEquals(expectedBanner, actualVideo.banner());
        assertEquals(expectedThumbnail, actualVideo.thumbnail());
        assertEquals(expectedThumbnailHalf, actualVideo.thumbnailHalf());
        assertEquals(expectedTrailer, actualVideo.trailer());
        assertEquals(expectedVideo, actualVideo.video());
        assertEquals(aVideo.getCreatedAt(), actualVideo.createdAt());
        assertEquals(aVideo.getUpdatedAt(), actualVideo.updatedAt());
    }

    @Test
    void Given_InvalidId_When_CallsGetVideo_Should_ReturnNotFound() {
        final var expectedErrorMessage = "Video with id 123 was not found";

        final var expectedId = VideoID.from("123");

        when(videoGateway.findById(any()))
                .thenReturn(Optional.empty());

        final var actualError = assertThrows(
                NotFoundException.class,
                () -> this.useCase.execute(expectedId.getValue()));

        assertEquals(expectedErrorMessage, actualError.getMessage());
    }

    private ImageMedia imageMedia(final Resource.Type type) {
        final var checksum = UUID.randomUUID().toString();
        return ImageMedia.with(
                checksum,
                type.name().toLowerCase(),
                "/images/" + checksum);
    }

    private VideoMedia videoMedia(final Resource.Type type) {
        final var checksum = UUID.randomUUID().toString();
        return VideoMedia.with(
                checksum,
                type.name().toLowerCase(),
                "/images/" + checksum,
                "",
                MediaStatus.PENDING);
    }

}
