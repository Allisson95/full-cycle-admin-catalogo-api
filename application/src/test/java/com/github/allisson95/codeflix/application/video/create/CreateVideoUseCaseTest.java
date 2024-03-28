package com.github.allisson95.codeflix.application.video.create;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.github.allisson95.codeflix.application.UseCaseTest;
import com.github.allisson95.codeflix.domain.Fixture;
import com.github.allisson95.codeflix.domain.castmember.CastMemberGateway;
import com.github.allisson95.codeflix.domain.castmember.CastMemberID;
import com.github.allisson95.codeflix.domain.category.CategoryGateway;
import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.domain.exceptions.DomainException;
import com.github.allisson95.codeflix.domain.exceptions.InternalErrorException;
import com.github.allisson95.codeflix.domain.genre.GenreGateway;
import com.github.allisson95.codeflix.domain.genre.GenreID;
import com.github.allisson95.codeflix.domain.utils.IdUtils;
import com.github.allisson95.codeflix.domain.video.ImageMedia;
import com.github.allisson95.codeflix.domain.video.MediaResourceGateway;
import com.github.allisson95.codeflix.domain.video.MediaStatus;
import com.github.allisson95.codeflix.domain.video.Resource;
import com.github.allisson95.codeflix.domain.video.VideoGateway;
import com.github.allisson95.codeflix.domain.video.VideoMedia;

class CreateVideoUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultCreateVideoUseCase useCase;

    @Mock
    private VideoGateway videoGateway;

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @Mock
    private CategoryGateway categoryGateway;

    @Mock
    private CastMemberGateway castMemberGateway;

    @Mock
    private GenreGateway genreGateway;

    @Override
    protected List<Object> getMocksForClean() {
        return List.of(
                videoGateway,
                mediaResourceGateway,
                categoryGateway,
                castMemberGateway,
                genreGateway);
    }

    @Test
    void Given_AValidCommand_When_CallsCreateVideo_Should_ReturnVideoId() {
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

        final Resource expectedBanner = Fixture.Videos.resource(Resource.Type.BANNER);
        final Resource expectedThumbnail = Fixture.Videos.resource(Resource.Type.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixture.Videos.resource(Resource.Type.THUMBNAIL_HALF);
        final Resource expectedTrailer = Fixture.Videos.resource(Resource.Type.TRAILER);
        final Resource expectedVideo = Fixture.Videos.resource(Resource.Type.VIDEO);

        final var aCommand = CreateVideoCommand.with(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt.getValue(),
                expectedDuration,
                expectedRating.getName(),
                expectedOpened,
                expectedPublished,
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedCastMembers),
                expectedBanner,
                expectedThumbnail,
                expectedThumbnailHalf,
                expectedTrailer,
                expectedVideo);

        when(categoryGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCategories));

        when(castMemberGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCastMembers));

        when(genreGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedGenres));

        when(mediaResourceGateway.storeImage(any(), any()))
                .then(it -> {
                    final var aResource = it.getArgument(1, Resource.class);
                    return ImageMedia.with(IdUtils.uuid(), aResource.name(), "/images");
                });

        when(mediaResourceGateway.storeVideo(any(), any()))
                .then(it -> {
                    final var aResource = it.getArgument(1, Resource.class);
                    return VideoMedia.with(IdUtils.uuid(), aResource.name(), "/videos", "/encoded", MediaStatus.PENDING);
                });

        when(videoGateway.create(any()))
                .thenAnswer(returnsFirstArg());

        final var actualResult = useCase.execute(aCommand);

        assertNotNull(actualResult);
        assertNotNull(actualResult.id());

        verify(videoGateway).create(argThat(actualVideo -> Objects.equals(expectedTitle, actualVideo.getTitle())
                && Objects.equals(expectedDescription, actualVideo.getDescription())
                && Objects.equals(expectedLaunchedAt, actualVideo.getLaunchedAt())
                && Objects.equals(expectedDuration, actualVideo.getDuration())
                && Objects.equals(expectedRating, actualVideo.getRating())
                && Objects.equals(expectedOpened, actualVideo.isOpened())
                && Objects.equals(expectedPublished, actualVideo.isPublished())
                && Objects.equals(expectedCategories, actualVideo.getCategories())
                && Objects.equals(expectedGenres, actualVideo.getGenres())
                && Objects.equals(expectedCastMembers, actualVideo.getCastMembers())
                && actualVideo.getBanner().isPresent()
                && Objects.equals(expectedBanner.name(), actualVideo.getBanner().get().name())
                && actualVideo.getThumbnail().isPresent()
                && Objects.equals(expectedThumbnail.name(), actualVideo.getThumbnail().get().name())
                && actualVideo.getThumbnailHalf().isPresent()
                && Objects.equals(expectedThumbnailHalf.name(), actualVideo.getThumbnailHalf().get().name())
                && actualVideo.getTrailer().isPresent()
                && Objects.equals(expectedTrailer.name(), actualVideo.getTrailer().get().name())
                && actualVideo.getVideo().isPresent()
                && Objects.equals(expectedVideo.name(), actualVideo.getVideo().get().name())));
    }

    @Test
    void Given_AValidCommandWithoutCategories_When_CallsCreateVideo_Should_ReturnVideoID() {
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchedAt = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.<CategoryID>of();
        final var expectedGenres = Set.<GenreID>of(Fixture.Genres.random().getId());
        final var expectedCastMembers = Set.<CastMemberID>of(
                Fixture.CastMembers.clintEastwood().getId(),
                Fixture.CastMembers.morganFreeman().getId());

        final Resource expectedBanner = Fixture.Videos.resource(Resource.Type.BANNER);
        final Resource expectedThumbnail = Fixture.Videos.resource(Resource.Type.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixture.Videos.resource(Resource.Type.THUMBNAIL_HALF);
        final Resource expectedTrailer = Fixture.Videos.resource(Resource.Type.TRAILER);
        final Resource expectedVideo = Fixture.Videos.resource(Resource.Type.VIDEO);

        final var aCommand = CreateVideoCommand.with(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt.getValue(),
                expectedDuration,
                expectedRating.getName(),
                expectedOpened,
                expectedPublished,
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedCastMembers),
                expectedBanner,
                expectedThumbnail,
                expectedThumbnailHalf,
                expectedTrailer,
                expectedVideo);

        when(castMemberGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCastMembers));

        when(genreGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedGenres));

        when(mediaResourceGateway.storeImage(any(), any()))
                .then(it -> {
                    final var aResource = it.getArgument(1, Resource.class);
                    return ImageMedia.with(IdUtils.uuid(), aResource.name(), "/images");
                });

        when(mediaResourceGateway.storeVideo(any(), any()))
                .then(it -> {
                    final var aResource = it.getArgument(1, Resource.class);
                    return VideoMedia.with(IdUtils.uuid(), aResource.name(), "/videos", "/encoded", MediaStatus.PENDING);
                });

        when(videoGateway.create(any()))
                .thenAnswer(returnsFirstArg());

        final var actualResult = useCase.execute(aCommand);

        assertNotNull(actualResult);
        assertNotNull(actualResult.id());

        verify(videoGateway).create(argThat(actualVideo -> Objects.equals(expectedTitle, actualVideo.getTitle())
                && Objects.equals(expectedDescription, actualVideo.getDescription())
                && Objects.equals(expectedLaunchedAt, actualVideo.getLaunchedAt())
                && Objects.equals(expectedDuration, actualVideo.getDuration())
                && Objects.equals(expectedRating, actualVideo.getRating())
                && Objects.equals(expectedOpened, actualVideo.isOpened())
                && Objects.equals(expectedPublished, actualVideo.isPublished())
                && Objects.equals(expectedCategories, actualVideo.getCategories())
                && Objects.equals(expectedGenres, actualVideo.getGenres())
                && Objects.equals(expectedCastMembers, actualVideo.getCastMembers())
                && actualVideo.getBanner().isPresent()
                && Objects.equals(expectedBanner.name(), actualVideo.getBanner().get().name())
                && actualVideo.getThumbnail().isPresent()
                && Objects.equals(expectedThumbnail.name(), actualVideo.getThumbnail().get().name())
                && actualVideo.getThumbnailHalf().isPresent()
                && Objects.equals(expectedThumbnailHalf.name(), actualVideo.getThumbnailHalf().get().name())
                && actualVideo.getTrailer().isPresent()
                && Objects.equals(expectedTrailer.name(), actualVideo.getTrailer().get().name())
                && actualVideo.getVideo().isPresent()
                && Objects.equals(expectedVideo.name(), actualVideo.getVideo().get().name())));
    }

    @Test
    void Given_AValidCommandWithoutCastMembers_When_CallsCreateVideo_Should_ReturnVideoID() {
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchedAt = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.<CategoryID>of(Fixture.Categories.random().getId());
        final var expectedGenres = Set.<GenreID>of(Fixture.Genres.random().getId());
        final var expectedCastMembers = Set.<CastMemberID>of();

        final Resource expectedBanner = Fixture.Videos.resource(Resource.Type.BANNER);
        final Resource expectedThumbnail = Fixture.Videos.resource(Resource.Type.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixture.Videos.resource(Resource.Type.THUMBNAIL_HALF);
        final Resource expectedTrailer = Fixture.Videos.resource(Resource.Type.TRAILER);
        final Resource expectedVideo = Fixture.Videos.resource(Resource.Type.VIDEO);

        final var aCommand = CreateVideoCommand.with(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt.getValue(),
                expectedDuration,
                expectedRating.getName(),
                expectedOpened,
                expectedPublished,
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedCastMembers),
                expectedBanner,
                expectedThumbnail,
                expectedThumbnailHalf,
                expectedTrailer,
                expectedVideo);

        when(categoryGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCategories));

        when(genreGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedGenres));

        when(mediaResourceGateway.storeImage(any(), any()))
                .then(it -> {
                    final var aResource = it.getArgument(1, Resource.class);
                    return ImageMedia.with(IdUtils.uuid(), aResource.name(), "/images");
                });

        when(mediaResourceGateway.storeVideo(any(), any()))
                .then(it -> {
                    final var aResource = it.getArgument(1, Resource.class);
                    return VideoMedia.with(IdUtils.uuid(), aResource.name(), "/videos", "/encoded", MediaStatus.PENDING);
                });

        when(videoGateway.create(any()))
                .thenAnswer(returnsFirstArg());

        final var actualResult = useCase.execute(aCommand);

        assertNotNull(actualResult);
        assertNotNull(actualResult.id());

        verify(videoGateway).create(argThat(actualVideo -> Objects.equals(expectedTitle, actualVideo.getTitle())
                && Objects.equals(expectedDescription, actualVideo.getDescription())
                && Objects.equals(expectedLaunchedAt, actualVideo.getLaunchedAt())
                && Objects.equals(expectedDuration, actualVideo.getDuration())
                && Objects.equals(expectedRating, actualVideo.getRating())
                && Objects.equals(expectedOpened, actualVideo.isOpened())
                && Objects.equals(expectedPublished, actualVideo.isPublished())
                && Objects.equals(expectedCategories, actualVideo.getCategories())
                && Objects.equals(expectedGenres, actualVideo.getGenres())
                && Objects.equals(expectedCastMembers, actualVideo.getCastMembers())
                && actualVideo.getBanner().isPresent()
                && Objects.equals(expectedBanner.name(), actualVideo.getBanner().get().name())
                && actualVideo.getThumbnail().isPresent()
                && Objects.equals(expectedThumbnail.name(), actualVideo.getThumbnail().get().name())
                && actualVideo.getThumbnailHalf().isPresent()
                && Objects.equals(expectedThumbnailHalf.name(), actualVideo.getThumbnailHalf().get().name())
                && actualVideo.getTrailer().isPresent()
                && Objects.equals(expectedTrailer.name(), actualVideo.getTrailer().get().name())
                && actualVideo.getVideo().isPresent()
                && Objects.equals(expectedVideo.name(), actualVideo.getVideo().get().name())));
    }

    @Test
    void Given_AValidCommandWithoutGenres_When_CallsCreateVideo_Should_ReturnVideoID() {
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchedAt = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.<CategoryID>of(Fixture.Categories.random().getId());
        final var expectedGenres = Set.<GenreID>of();
        final var expectedCastMembers = Set.<CastMemberID>of(
                Fixture.CastMembers.clintEastwood().getId(),
                Fixture.CastMembers.morganFreeman().getId());

        final Resource expectedBanner = Fixture.Videos.resource(Resource.Type.BANNER);
        final Resource expectedThumbnail = Fixture.Videos.resource(Resource.Type.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixture.Videos.resource(Resource.Type.THUMBNAIL_HALF);
        final Resource expectedTrailer = Fixture.Videos.resource(Resource.Type.TRAILER);
        final Resource expectedVideo = Fixture.Videos.resource(Resource.Type.VIDEO);

        final var aCommand = CreateVideoCommand.with(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt.getValue(),
                expectedDuration,
                expectedRating.getName(),
                expectedOpened,
                expectedPublished,
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedCastMembers),
                expectedBanner,
                expectedThumbnail,
                expectedThumbnailHalf,
                expectedTrailer,
                expectedVideo);

        when(categoryGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCategories));

        when(castMemberGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCastMembers));

        when(mediaResourceGateway.storeImage(any(), any()))
                .then(it -> {
                    final var aResource = it.getArgument(1, Resource.class);
                    return ImageMedia.with(IdUtils.uuid(), aResource.name(), "/images");
                });

        when(mediaResourceGateway.storeVideo(any(), any()))
                .then(it -> {
                    final var aResource = it.getArgument(1, Resource.class);
                    return VideoMedia.with(IdUtils.uuid(), aResource.name(), "/videos", "/encoded", MediaStatus.PENDING);
                });

        when(videoGateway.create(any()))
                .thenAnswer(returnsFirstArg());

        final var actualResult = useCase.execute(aCommand);

        assertNotNull(actualResult);
        assertNotNull(actualResult.id());

        verify(videoGateway).create(argThat(actualVideo -> Objects.equals(expectedTitle, actualVideo.getTitle())
                && Objects.equals(expectedDescription, actualVideo.getDescription())
                && Objects.equals(expectedLaunchedAt, actualVideo.getLaunchedAt())
                && Objects.equals(expectedDuration, actualVideo.getDuration())
                && Objects.equals(expectedRating, actualVideo.getRating())
                && Objects.equals(expectedOpened, actualVideo.isOpened())
                && Objects.equals(expectedPublished, actualVideo.isPublished())
                && Objects.equals(expectedCategories, actualVideo.getCategories())
                && Objects.equals(expectedGenres, actualVideo.getGenres())
                && Objects.equals(expectedCastMembers, actualVideo.getCastMembers())
                && actualVideo.getBanner().isPresent()
                && Objects.equals(expectedBanner.name(), actualVideo.getBanner().get().name())
                && actualVideo.getThumbnail().isPresent()
                && Objects.equals(expectedThumbnail.name(), actualVideo.getThumbnail().get().name())
                && actualVideo.getThumbnailHalf().isPresent()
                && Objects.equals(expectedThumbnailHalf.name(), actualVideo.getThumbnailHalf().get().name())
                && actualVideo.getTrailer().isPresent()
                && Objects.equals(expectedTrailer.name(), actualVideo.getTrailer().get().name())
                && actualVideo.getVideo().isPresent()
                && Objects.equals(expectedVideo.name(), actualVideo.getVideo().get().name())));
    }

    @Test
    void Given_AValidCommandWithoutResources_When_CallsCreateVideo_Should_ReturnVideoId() {
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

        final Resource expectedBanner = null;
        final Resource expectedThumbnail = null;
        final Resource expectedThumbnailHalf = null;
        final Resource expectedTrailer = null;
        final Resource expectedVideo = null;

        final var aCommand = CreateVideoCommand.with(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt.getValue(),
                expectedDuration,
                expectedRating.getName(),
                expectedOpened,
                expectedPublished,
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedCastMembers),
                expectedBanner,
                expectedThumbnail,
                expectedThumbnailHalf,
                expectedTrailer,
                expectedVideo);

        when(categoryGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCategories));

        when(castMemberGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCastMembers));

        when(genreGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedGenres));

        when(videoGateway.create(any()))
                .thenAnswer(returnsFirstArg());

        final var actualResult = useCase.execute(aCommand);

        assertNotNull(actualResult);
        assertNotNull(actualResult.id());

        verify(videoGateway).create(argThat(actualVideo -> Objects.equals(expectedTitle, actualVideo.getTitle())
                && Objects.equals(expectedDescription, actualVideo.getDescription())
                && Objects.equals(expectedLaunchedAt, actualVideo.getLaunchedAt())
                && Objects.equals(expectedDuration, actualVideo.getDuration())
                && Objects.equals(expectedRating, actualVideo.getRating())
                && Objects.equals(expectedOpened, actualVideo.isOpened())
                && Objects.equals(expectedPublished, actualVideo.isPublished())
                && Objects.equals(expectedCategories, actualVideo.getCategories())
                && Objects.equals(expectedGenres, actualVideo.getGenres())
                && Objects.equals(expectedCastMembers, actualVideo.getCastMembers())
                && actualVideo.getBanner().isEmpty()
                && actualVideo.getThumbnail().isEmpty()
                && actualVideo.getThumbnailHalf().isEmpty()
                && actualVideo.getTrailer().isEmpty()
                && actualVideo.getVideo().isEmpty()));
    }

    @Test
    void Given_AInvalidNullTitle_When_CallsCreateVideo_Should_ReturnDomainException() {
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'title' should not be null";

        final String expectedTitle = null;
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchedAt = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.<CategoryID>of();
        final var expectedGenres = Set.<GenreID>of();
        final var expectedCastMembers = Set.<CastMemberID>of();

        final Resource expectedBanner = null;
        final Resource expectedThumbnail = null;
        final Resource expectedThumbnailHalf = null;
        final Resource expectedTrailer = null;
        final Resource expectedVideo = null;

        final var aCommand = CreateVideoCommand.with(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt.getValue(),
                expectedDuration,
                expectedRating.getName(),
                expectedOpened,
                expectedPublished,
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedCastMembers),
                expectedBanner,
                expectedThumbnail,
                expectedThumbnailHalf,
                expectedTrailer,
                expectedVideo);

        final var actualResult = assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        assertNotNull(actualResult);
        assertEquals(expectedErrorCount, actualResult.getErrors().size());
        assertEquals(expectedErrorMessage, actualResult.getErrors().get(0).message());

        verify(categoryGateway, times(0)).existsByIds(any());
        verify(castMemberGateway, times(0)).existsByIds(any());
        verify(genreGateway, times(0)).existsByIds(any());
        verify(mediaResourceGateway, times(0)).storeImage(any(), any());
        verify(mediaResourceGateway, times(0)).storeVideo(any(), any());
        verify(videoGateway, times(0)).create(any());
    }

    @Test
    void Given_AInvalidEmptyTitle_When_CallsCreateVideo_Should_ReturnDomainException() {
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'title' should not be empty";

        final var expectedTitle = " ";
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchedAt = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.<CategoryID>of();
        final var expectedGenres = Set.<GenreID>of();
        final var expectedCastMembers = Set.<CastMemberID>of();

        final Resource expectedBanner = null;
        final Resource expectedThumbnail = null;
        final Resource expectedThumbnailHalf = null;
        final Resource expectedTrailer = null;
        final Resource expectedVideo = null;

        final var aCommand = CreateVideoCommand.with(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt.getValue(),
                expectedDuration,
                expectedRating.getName(),
                expectedOpened,
                expectedPublished,
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedCastMembers),
                expectedBanner,
                expectedThumbnail,
                expectedThumbnailHalf,
                expectedTrailer,
                expectedVideo);

        final var actualResult = assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        assertNotNull(actualResult);
        assertEquals(expectedErrorCount, actualResult.getErrors().size());
        assertEquals(expectedErrorMessage, actualResult.getErrors().get(0).message());

        verify(categoryGateway, times(0)).existsByIds(any());
        verify(castMemberGateway, times(0)).existsByIds(any());
        verify(genreGateway, times(0)).existsByIds(any());
        verify(mediaResourceGateway, times(0)).storeImage(any(), any());
        verify(mediaResourceGateway, times(0)).storeVideo(any(), any());
        verify(videoGateway, times(0)).create(any());
    }

    @Test
    void Given_AInvalidNullDescription_When_CallsCreateVideo_Should_ReturnDomainException() {
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'description' should not be null";

        final var expectedTitle = Fixture.title();
        final String expectedDescription = null;
        final var expectedLaunchedAt = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.<CategoryID>of();
        final var expectedGenres = Set.<GenreID>of();
        final var expectedCastMembers = Set.<CastMemberID>of();

        final Resource expectedBanner = null;
        final Resource expectedThumbnail = null;
        final Resource expectedThumbnailHalf = null;
        final Resource expectedTrailer = null;
        final Resource expectedVideo = null;

        final var aCommand = CreateVideoCommand.with(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt.getValue(),
                expectedDuration,
                expectedRating.getName(),
                expectedOpened,
                expectedPublished,
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedCastMembers),
                expectedBanner,
                expectedThumbnail,
                expectedThumbnailHalf,
                expectedTrailer,
                expectedVideo);

        final var actualResult = assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        assertNotNull(actualResult);
        assertEquals(expectedErrorCount, actualResult.getErrors().size());
        assertEquals(expectedErrorMessage, actualResult.getErrors().get(0).message());

        verify(categoryGateway, times(0)).existsByIds(any());
        verify(castMemberGateway, times(0)).existsByIds(any());
        verify(genreGateway, times(0)).existsByIds(any());
        verify(mediaResourceGateway, times(0)).storeImage(any(), any());
        verify(mediaResourceGateway, times(0)).storeVideo(any(), any());
        verify(videoGateway, times(0)).create(any());
    }

    @Test
    void Given_AInvalidEmptyDescription_When_CallsCreateVideo_Should_ReturnDomainException() {
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'description' should not be empty";

        final var expectedTitle = Fixture.title();
        final String expectedDescription = " ";
        final var expectedLaunchedAt = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.<CategoryID>of();
        final var expectedGenres = Set.<GenreID>of();
        final var expectedCastMembers = Set.<CastMemberID>of();

        final Resource expectedBanner = null;
        final Resource expectedThumbnail = null;
        final Resource expectedThumbnailHalf = null;
        final Resource expectedTrailer = null;
        final Resource expectedVideo = null;

        final var aCommand = CreateVideoCommand.with(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt.getValue(),
                expectedDuration,
                expectedRating.getName(),
                expectedOpened,
                expectedPublished,
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedCastMembers),
                expectedBanner,
                expectedThumbnail,
                expectedThumbnailHalf,
                expectedTrailer,
                expectedVideo);

        final var actualResult = assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        assertNotNull(actualResult);
        assertEquals(expectedErrorCount, actualResult.getErrors().size());
        assertEquals(expectedErrorMessage, actualResult.getErrors().get(0).message());

        verify(categoryGateway, times(0)).existsByIds(any());
        verify(castMemberGateway, times(0)).existsByIds(any());
        verify(genreGateway, times(0)).existsByIds(any());
        verify(mediaResourceGateway, times(0)).storeImage(any(), any());
        verify(mediaResourceGateway, times(0)).storeVideo(any(), any());
        verify(videoGateway, times(0)).create(any());
    }

    @Test
    void Given_AInvalidNullLauncedAt_When_CallsCreateVideo_Should_ReturnDomainException() {
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'launchedAt' should not be null";

        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final Integer expectedLaunchedAt = null;
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.<CategoryID>of();
        final var expectedGenres = Set.<GenreID>of();
        final var expectedCastMembers = Set.<CastMemberID>of();

        final Resource expectedBanner = null;
        final Resource expectedThumbnail = null;
        final Resource expectedThumbnailHalf = null;
        final Resource expectedTrailer = null;
        final Resource expectedVideo = null;

        final var aCommand = CreateVideoCommand.with(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedRating.getName(),
                expectedOpened,
                expectedPublished,
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedCastMembers),
                expectedBanner,
                expectedThumbnail,
                expectedThumbnailHalf,
                expectedTrailer,
                expectedVideo);

        final var actualResult = assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        assertNotNull(actualResult);
        assertEquals(expectedErrorCount, actualResult.getErrors().size());
        assertEquals(expectedErrorMessage, actualResult.getErrors().get(0).message());

        verify(categoryGateway, times(0)).existsByIds(any());
        verify(castMemberGateway, times(0)).existsByIds(any());
        verify(genreGateway, times(0)).existsByIds(any());
        verify(mediaResourceGateway, times(0)).storeImage(any(), any());
        verify(mediaResourceGateway, times(0)).storeVideo(any(), any());
        verify(videoGateway, times(0)).create(any());
    }

    @Test
    void Given_AInvalidNullRating_When_CallsCreateVideo_Should_ReturnDomainException() {
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'rating' should not be null";

        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchedAt = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final String expectedRating = null;
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.<CategoryID>of();
        final var expectedGenres = Set.<GenreID>of();
        final var expectedCastMembers = Set.<CastMemberID>of();

        final Resource expectedBanner = null;
        final Resource expectedThumbnail = null;
        final Resource expectedThumbnailHalf = null;
        final Resource expectedTrailer = null;
        final Resource expectedVideo = null;

        final var aCommand = CreateVideoCommand.with(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt.getValue(),
                expectedDuration,
                expectedRating,
                expectedOpened,
                expectedPublished,
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedCastMembers),
                expectedBanner,
                expectedThumbnail,
                expectedThumbnailHalf,
                expectedTrailer,
                expectedVideo);

        final var actualResult = assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        assertNotNull(actualResult);
        assertEquals(expectedErrorCount, actualResult.getErrors().size());
        assertEquals(expectedErrorMessage, actualResult.getErrors().get(0).message());

        verify(categoryGateway, times(0)).existsByIds(any());
        verify(castMemberGateway, times(0)).existsByIds(any());
        verify(genreGateway, times(0)).existsByIds(any());
        verify(mediaResourceGateway, times(0)).storeImage(any(), any());
        verify(mediaResourceGateway, times(0)).storeVideo(any(), any());
        verify(videoGateway, times(0)).create(any());
    }

    @Test
    void Given_AValidCommand_When_CallsCreateVideoAndSomeCategoriesDoesNotExists_Should_ReturnDomainException() {
        final var categoryId = Fixture.Categories.random().getId();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "Some categories could not be found: %s".formatted(categoryId.getValue());

        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchedAt = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.<CategoryID>of(categoryId);
        final var expectedGenres = Set.<GenreID>of(Fixture.Genres.random().getId());
        final var expectedCastMembers = Set.<CastMemberID>of(
                Fixture.CastMembers.clintEastwood().getId(),
                Fixture.CastMembers.morganFreeman().getId());

        final Resource expectedBanner = null;
        final Resource expectedThumbnail = null;
        final Resource expectedThumbnailHalf = null;
        final Resource expectedTrailer = null;
        final Resource expectedVideo = null;

        final var aCommand = CreateVideoCommand.with(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt.getValue(),
                expectedDuration,
                expectedRating.getName(),
                expectedOpened,
                expectedPublished,
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedCastMembers),
                expectedBanner,
                expectedThumbnail,
                expectedThumbnailHalf,
                expectedTrailer,
                expectedVideo);

        when(categoryGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>());

        when(castMemberGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCastMembers));

        when(genreGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedGenres));

        final var actualResult = assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        assertNotNull(actualResult);
        assertEquals(expectedErrorCount, actualResult.getErrors().size());
        assertEquals(expectedErrorMessage, actualResult.getErrors().get(0).message());

        verify(categoryGateway, times(1)).existsByIds(expectedCategories);
        verify(castMemberGateway, times(1)).existsByIds(expectedCastMembers);
        verify(genreGateway, times(1)).existsByIds(expectedGenres);
        verify(mediaResourceGateway, times(0)).storeImage(any(), any());
        verify(mediaResourceGateway, times(0)).storeVideo(any(), any());
        verify(videoGateway, times(0)).create(any());
    }

    @Test
    void Given_AValidCommand_When_CallsCreateVideoAndSomeCastMembersDoesNotExists_Should_ReturnDomainException() {
        final var castMemberId = Fixture.CastMembers.random().getId();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "Some cast members could not be found: %s".formatted(castMemberId.getValue());

        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchedAt = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.<CategoryID>of(Fixture.Categories.random().getId());
        final var expectedGenres = Set.<GenreID>of(Fixture.Genres.random().getId());
        final var expectedCastMembers = Set.<CastMemberID>of(castMemberId);

        final Resource expectedBanner = null;
        final Resource expectedThumbnail = null;
        final Resource expectedThumbnailHalf = null;
        final Resource expectedTrailer = null;
        final Resource expectedVideo = null;

        final var aCommand = CreateVideoCommand.with(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt.getValue(),
                expectedDuration,
                expectedRating.getName(),
                expectedOpened,
                expectedPublished,
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedCastMembers),
                expectedBanner,
                expectedThumbnail,
                expectedThumbnailHalf,
                expectedTrailer,
                expectedVideo);

        when(categoryGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCategories));

        when(castMemberGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>());

        when(genreGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedGenres));

        final var actualResult = assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        assertNotNull(actualResult);
        assertEquals(expectedErrorCount, actualResult.getErrors().size());
        assertEquals(expectedErrorMessage, actualResult.getErrors().get(0).message());

        verify(categoryGateway, times(1)).existsByIds(expectedCategories);
        verify(castMemberGateway, times(1)).existsByIds(expectedCastMembers);
        verify(genreGateway, times(1)).existsByIds(expectedGenres);
        verify(mediaResourceGateway, times(0)).storeImage(any(), any());
        verify(mediaResourceGateway, times(0)).storeVideo(any(), any());
        verify(videoGateway, times(0)).create(any());
    }

    @Test
    void Given_AValidCommand_When_CallsCreateVideoAndSomeGenresDoesNotExists_Should_ReturnDomainException() {
        final var genreId = Fixture.Genres.random().getId();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "Some genres could not be found: %s".formatted(genreId.getValue());

        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchedAt = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.<CategoryID>of(Fixture.Categories.random().getId());
        final var expectedGenres = Set.<GenreID>of(genreId);
        final var expectedCastMembers = Set.<CastMemberID>of(
                Fixture.CastMembers.clintEastwood().getId(),
                Fixture.CastMembers.morganFreeman().getId());

        final Resource expectedBanner = null;
        final Resource expectedThumbnail = null;
        final Resource expectedThumbnailHalf = null;
        final Resource expectedTrailer = null;
        final Resource expectedVideo = null;

        final var aCommand = CreateVideoCommand.with(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt.getValue(),
                expectedDuration,
                expectedRating.getName(),
                expectedOpened,
                expectedPublished,
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedCastMembers),
                expectedBanner,
                expectedThumbnail,
                expectedThumbnailHalf,
                expectedTrailer,
                expectedVideo);

        when(categoryGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCategories));

        when(castMemberGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCastMembers));

        when(genreGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>());

        final var actualResult = assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        assertNotNull(actualResult);
        assertEquals(expectedErrorCount, actualResult.getErrors().size());
        assertEquals(expectedErrorMessage, actualResult.getErrors().get(0).message());

        verify(categoryGateway, times(1)).existsByIds(expectedCategories);
        verify(castMemberGateway, times(1)).existsByIds(expectedCastMembers);
        verify(genreGateway, times(1)).existsByIds(expectedGenres);
        verify(mediaResourceGateway, times(0)).storeImage(any(), any());
        verify(mediaResourceGateway, times(0)).storeVideo(any(), any());
        verify(videoGateway, times(0)).create(any());
    }

    @Test
    void Given_AValidCommand_When_CallsCreateVideoThrowsException_Should_CallClearResources() {
        final var expectedErrorMessage = "An error on create video was observed [videoId:";

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

        final Resource expectedBanner = Fixture.Videos.resource(Resource.Type.BANNER);
        final Resource expectedThumbnail = Fixture.Videos.resource(Resource.Type.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixture.Videos.resource(Resource.Type.THUMBNAIL_HALF);
        final Resource expectedTrailer = Fixture.Videos.resource(Resource.Type.TRAILER);
        final Resource expectedVideo = Fixture.Videos.resource(Resource.Type.VIDEO);

        final var aCommand = CreateVideoCommand.with(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt.getValue(),
                expectedDuration,
                expectedRating.getName(),
                expectedOpened,
                expectedPublished,
                asString(expectedCategories),
                asString(expectedGenres),
                asString(expectedCastMembers),
                expectedBanner,
                expectedThumbnail,
                expectedThumbnailHalf,
                expectedTrailer,
                expectedVideo);

        when(categoryGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCategories));

        when(castMemberGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCastMembers));

        when(genreGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedGenres));

        when(mediaResourceGateway.storeImage(any(), any()))
                .then(it -> {
                    final var aResource = it.getArgument(1, Resource.class);
                    return ImageMedia.with(IdUtils.uuid(), aResource.name(), "/images");
                });

        when(mediaResourceGateway.storeVideo(any(), any()))
                .then(it -> {
                    final var aResource = it.getArgument(1, Resource.class);
                    return VideoMedia.with(IdUtils.uuid(), aResource.name(), "/videos", "/encoded", MediaStatus.PENDING);
                });

        when(videoGateway.create(any()))
                .thenThrow(new RuntimeException("Generic Error"));

        final var actualResult = assertThrows(InternalErrorException.class, () -> useCase.execute(aCommand));

        assertNotNull(actualResult);
        assertTrue(actualResult.getMessage().startsWith(expectedErrorMessage));

        verify(categoryGateway, times(1)).existsByIds(expectedCategories);
        verify(castMemberGateway, times(1)).existsByIds(expectedCastMembers);
        verify(genreGateway, times(1)).existsByIds(expectedGenres);
        verify(mediaResourceGateway, times(3)).storeImage(any(), any());
        verify(mediaResourceGateway, times(2)).storeVideo(any(), any());
        verify(videoGateway).create(argThat(actualVideo -> Objects.equals(expectedTitle, actualVideo.getTitle())
                && Objects.equals(expectedDescription, actualVideo.getDescription())
                && Objects.equals(expectedLaunchedAt, actualVideo.getLaunchedAt())
                && Objects.equals(expectedDuration, actualVideo.getDuration())
                && Objects.equals(expectedRating, actualVideo.getRating())
                && Objects.equals(expectedOpened, actualVideo.isOpened())
                && Objects.equals(expectedPublished, actualVideo.isPublished())
                && Objects.equals(expectedCategories, actualVideo.getCategories())
                && Objects.equals(expectedGenres, actualVideo.getGenres())
                && Objects.equals(expectedCastMembers, actualVideo.getCastMembers())
                && actualVideo.getBanner().isPresent()
                && Objects.equals(expectedBanner.name(), actualVideo.getBanner().get().name())
                && actualVideo.getThumbnail().isPresent()
                && Objects.equals(expectedThumbnail.name(), actualVideo.getThumbnail().get().name())
                && actualVideo.getThumbnailHalf().isPresent()
                && Objects.equals(expectedThumbnailHalf.name(), actualVideo.getThumbnailHalf().get().name())
                && actualVideo.getTrailer().isPresent()
                && Objects.equals(expectedTrailer.name(), actualVideo.getTrailer().get().name())
                && actualVideo.getVideo().isPresent()
                && Objects.equals(expectedVideo.name(), actualVideo.getVideo().get().name())));
        verify(mediaResourceGateway, times(1)).clearResources(any());
    }

}
