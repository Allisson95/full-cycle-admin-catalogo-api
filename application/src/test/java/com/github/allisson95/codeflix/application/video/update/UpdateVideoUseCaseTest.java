package com.github.allisson95.codeflix.application.video.update;

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
import java.util.Optional;
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
import com.github.allisson95.codeflix.domain.resource.Resource;
import com.github.allisson95.codeflix.domain.video.ImageMedia;
import com.github.allisson95.codeflix.domain.video.MediaResourceGateway;
import com.github.allisson95.codeflix.domain.video.Video;
import com.github.allisson95.codeflix.domain.video.VideoGateway;
import com.github.allisson95.codeflix.domain.video.VideoMedia;
import com.github.allisson95.codeflix.domain.video.VideoMediaType;
import com.github.allisson95.codeflix.domain.video.VideoResource;

class UpdateVideoUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultUpdateVideoUseCase useCase;

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
    void Given_AValidCommand_When_CallsUpdateVideo_Should_ReturnVideoId() {
        final var aVideo = Fixture.Videos.random();

        final var expectedId = aVideo.getId();
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

        final Resource expectedBanner = Fixture.Videos.resource(VideoMediaType.BANNER);
        final Resource expectedThumbnail = Fixture.Videos.resource(VideoMediaType.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixture.Videos.resource(VideoMediaType.THUMBNAIL_HALF);
        final Resource expectedTrailer = Fixture.Videos.resource(VideoMediaType.TRAILER);
        final Resource expectedVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);

        final var aCommand = UpdateVideoCommand.with(
                expectedId.getValue(),
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

        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(Video.with(aVideo)));

        when(categoryGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCategories));

        when(castMemberGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCastMembers));

        when(genreGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedGenres));

        mockImage();

        mockVideo();

        when(videoGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var actualResult = useCase.execute(aCommand);

        assertNotNull(actualResult);
        assertNotNull(actualResult.id());

        verify(videoGateway).findById(expectedId);
        verify(videoGateway)
                .update(argThat(actualVideo -> Objects.equals(expectedId.getValue(), actualVideo.getId().getValue())
                        && Objects.equals(expectedTitle, actualVideo.getTitle())
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
                        && Objects.equals(expectedVideo.name(), actualVideo.getVideo().get().name())
                        && Objects.equals(aVideo.getCreatedAt(), actualVideo.getCreatedAt())
                        && aVideo.getCreatedAt().isBefore(actualVideo.getUpdatedAt())));
    }

    @Test
    void Given_AValidCommandWithoutCategories_When_CallsUpdateVideo_Should_ReturnVideoID() {
        final var aVideo = Fixture.Videos.random();

        final var expectedId = aVideo.getId();
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

        final Resource expectedBanner = Fixture.Videos.resource(VideoMediaType.BANNER);
        final Resource expectedThumbnail = Fixture.Videos.resource(VideoMediaType.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixture.Videos.resource(VideoMediaType.THUMBNAIL_HALF);
        final Resource expectedTrailer = Fixture.Videos.resource(VideoMediaType.TRAILER);
        final Resource expectedVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);

        final var aCommand = UpdateVideoCommand.with(
                expectedId.getValue(),
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

        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(Video.with(aVideo)));

        when(castMemberGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCastMembers));

        when(genreGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedGenres));

        mockImage();

        mockVideo();

        when(videoGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var actualResult = useCase.execute(aCommand);

        assertNotNull(actualResult);
        assertNotNull(actualResult.id());

        verify(videoGateway).update(argThat(actualVideo -> Objects.equals(expectedTitle, actualVideo.getTitle())
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
                && Objects.equals(expectedVideo.name(), actualVideo.getVideo().get().name())
                && Objects.equals(aVideo.getCreatedAt(), actualVideo.getCreatedAt())
                && aVideo.getCreatedAt().isBefore(actualVideo.getUpdatedAt())));
    }

    @Test
    void Given_AValidCommandWithoutCastMembers_When_CallsUpdateVideo_Should_ReturnVideoID() {
        final var aVideo = Fixture.Videos.random();

        final var expectedId = aVideo.getId();
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

        final Resource expectedBanner = Fixture.Videos.resource(VideoMediaType.BANNER);
        final Resource expectedThumbnail = Fixture.Videos.resource(VideoMediaType.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixture.Videos.resource(VideoMediaType.THUMBNAIL_HALF);
        final Resource expectedTrailer = Fixture.Videos.resource(VideoMediaType.TRAILER);
        final Resource expectedVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);

        final var aCommand = UpdateVideoCommand.with(
                expectedId.getValue(),
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

        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(Video.with(aVideo)));

        when(categoryGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCategories));

        when(genreGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedGenres));

        mockImage();

        mockVideo();

        when(videoGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var actualResult = useCase.execute(aCommand);

        assertNotNull(actualResult);
        assertNotNull(actualResult.id());

        verify(videoGateway).update(argThat(actualVideo -> Objects.equals(expectedTitle, actualVideo.getTitle())
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
                && Objects.equals(expectedVideo.name(), actualVideo.getVideo().get().name())
                && Objects.equals(aVideo.getCreatedAt(), actualVideo.getCreatedAt())
                && aVideo.getCreatedAt().isBefore(actualVideo.getUpdatedAt())));
    }

    @Test
    void Given_AValidCommandWithoutGenres_When_CallsUpdateVideo_Should_ReturnVideoID() {
        final var aVideo = Fixture.Videos.random();

        final var expectedId = aVideo.getId();
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

        final Resource expectedBanner = Fixture.Videos.resource(VideoMediaType.BANNER);
        final Resource expectedThumbnail = Fixture.Videos.resource(VideoMediaType.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixture.Videos.resource(VideoMediaType.THUMBNAIL_HALF);
        final Resource expectedTrailer = Fixture.Videos.resource(VideoMediaType.TRAILER);
        final Resource expectedVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);

        final var aCommand = UpdateVideoCommand.with(
                expectedId.getValue(),
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

        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(Video.with(aVideo)));

        when(categoryGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCategories));

        when(castMemberGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCastMembers));

        mockImage();

        mockVideo();

        when(videoGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var actualResult = useCase.execute(aCommand);

        assertNotNull(actualResult);
        assertNotNull(actualResult.id());

        verify(videoGateway).update(argThat(actualVideo -> Objects.equals(expectedTitle, actualVideo.getTitle())
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
                && Objects.equals(expectedVideo.name(), actualVideo.getVideo().get().name())
                && Objects.equals(aVideo.getCreatedAt(), actualVideo.getCreatedAt())
                && aVideo.getCreatedAt().isBefore(actualVideo.getUpdatedAt())));
    }

    @Test
    void Given_AValidCommandWithoutResources_When_CallsUpdateVideo_Should_ReturnVideoId() {
        final var aVideo = Fixture.Videos.random();

        final var expectedId = aVideo.getId();
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

        final var aCommand = UpdateVideoCommand.with(
                expectedId.getValue(),
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

        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(Video.with(aVideo)));

        when(categoryGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCategories));

        when(castMemberGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCastMembers));

        when(genreGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedGenres));

        when(videoGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var actualResult = useCase.execute(aCommand);

        assertNotNull(actualResult);
        assertNotNull(actualResult.id());

        verify(videoGateway).update(argThat(actualVideo -> Objects.equals(expectedTitle, actualVideo.getTitle())
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
                && actualVideo.getVideo().isEmpty()
                && Objects.equals(aVideo.getCreatedAt(), actualVideo.getCreatedAt())
                && aVideo.getCreatedAt().isBefore(actualVideo.getUpdatedAt())));
    }

    @Test
    void Given_AInvalidNullTitle_When_CallsUpdateVideo_Should_ReturnDomainException() {
        final var aVideo = Fixture.Videos.random();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'title' should not be null";

        final var expectedId = aVideo.getId();
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

        final var aCommand = UpdateVideoCommand.with(
                expectedId.getValue(),
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

        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(Video.with(aVideo)));

        final var actualResult = assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        assertNotNull(actualResult);
        assertEquals(expectedErrorCount, actualResult.getErrors().size());
        assertEquals(expectedErrorMessage, actualResult.getErrors().get(0).message());

        verify(videoGateway, times(1)).findById(any());
        verify(categoryGateway, times(0)).existsByIds(any());
        verify(castMemberGateway, times(0)).existsByIds(any());
        verify(genreGateway, times(0)).existsByIds(any());
        verify(mediaResourceGateway, times(0)).storeImage(any(), any());
        verify(mediaResourceGateway, times(0)).storeVideo(any(), any());
        verify(videoGateway, times(0)).update(any());
    }

    @Test
    void Given_AInvalidEmptyTitle_When_CallsUpdateVideo_Should_ReturnDomainException() {
        final var aVideo = Fixture.Videos.random();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'title' should not be empty";

        final var expectedId = aVideo.getId();
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

        final var aCommand = UpdateVideoCommand.with(
                expectedId.getValue(),
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

        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(Video.with(aVideo)));

        final var actualResult = assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        assertNotNull(actualResult);
        assertEquals(expectedErrorCount, actualResult.getErrors().size());
        assertEquals(expectedErrorMessage, actualResult.getErrors().get(0).message());

        verify(videoGateway, times(1)).findById(any());
        verify(categoryGateway, times(0)).existsByIds(any());
        verify(castMemberGateway, times(0)).existsByIds(any());
        verify(genreGateway, times(0)).existsByIds(any());
        verify(mediaResourceGateway, times(0)).storeImage(any(), any());
        verify(mediaResourceGateway, times(0)).storeVideo(any(), any());
        verify(videoGateway, times(0)).update(any());
    }

    @Test
    void Given_AInvalidNullDescription_When_CallsUpdateVideo_Should_ReturnDomainException() {
        final var aVideo = Fixture.Videos.random();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'description' should not be null";

        final var expectedId = aVideo.getId();
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

        final var aCommand = UpdateVideoCommand.with(
                expectedId.getValue(),
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

        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(Video.with(aVideo)));

        final var actualResult = assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        assertNotNull(actualResult);
        assertEquals(expectedErrorCount, actualResult.getErrors().size());
        assertEquals(expectedErrorMessage, actualResult.getErrors().get(0).message());

        verify(videoGateway, times(1)).findById(any());
        verify(categoryGateway, times(0)).existsByIds(any());
        verify(castMemberGateway, times(0)).existsByIds(any());
        verify(genreGateway, times(0)).existsByIds(any());
        verify(mediaResourceGateway, times(0)).storeImage(any(), any());
        verify(mediaResourceGateway, times(0)).storeVideo(any(), any());
        verify(videoGateway, times(0)).update(any());
    }

    @Test
    void Given_AInvalidEmptyDescription_When_CallsUpdateVideo_Should_ReturnDomainException() {
        final var aVideo = Fixture.Videos.random();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'description' should not be empty";

        final var expectedId = aVideo.getId();
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

        final var aCommand = UpdateVideoCommand.with(
                expectedId.getValue(),
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

        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(Video.with(aVideo)));

        final var actualResult = assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        assertNotNull(actualResult);
        assertEquals(expectedErrorCount, actualResult.getErrors().size());
        assertEquals(expectedErrorMessage, actualResult.getErrors().get(0).message());

        verify(videoGateway, times(1)).findById(any());
        verify(categoryGateway, times(0)).existsByIds(any());
        verify(castMemberGateway, times(0)).existsByIds(any());
        verify(genreGateway, times(0)).existsByIds(any());
        verify(mediaResourceGateway, times(0)).storeImage(any(), any());
        verify(mediaResourceGateway, times(0)).storeVideo(any(), any());
        verify(videoGateway, times(0)).update(any());
    }

    @Test
    void Given_AInvalidNullLauncedAt_When_CallsUpdateVideo_Should_ReturnDomainException() {
        final var aVideo = Fixture.Videos.random();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'launchedAt' should not be null";

        final var expectedId = aVideo.getId();
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

        final var aCommand = UpdateVideoCommand.with(
                expectedId.getValue(),
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

        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(Video.with(aVideo)));

        final var actualResult = assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        assertNotNull(actualResult);
        assertEquals(expectedErrorCount, actualResult.getErrors().size());
        assertEquals(expectedErrorMessage, actualResult.getErrors().get(0).message());

        verify(videoGateway, times(1)).findById(any());
        verify(categoryGateway, times(0)).existsByIds(any());
        verify(castMemberGateway, times(0)).existsByIds(any());
        verify(genreGateway, times(0)).existsByIds(any());
        verify(mediaResourceGateway, times(0)).storeImage(any(), any());
        verify(mediaResourceGateway, times(0)).storeVideo(any(), any());
        verify(videoGateway, times(0)).update(any());
    }

    @Test
    void Given_AInvalidNullRating_When_CallsUpdateVideo_Should_ReturnDomainException() {
        final var aVideo = Fixture.Videos.random();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'rating' should not be null";

        final var expectedId = aVideo.getId();
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

        final var aCommand = UpdateVideoCommand.with(
                expectedId.getValue(),
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

        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(Video.with(aVideo)));

        final var actualResult = assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        assertNotNull(actualResult);
        assertEquals(expectedErrorCount, actualResult.getErrors().size());
        assertEquals(expectedErrorMessage, actualResult.getErrors().get(0).message());

        verify(videoGateway, times(1)).findById(any());
        verify(categoryGateway, times(0)).existsByIds(any());
        verify(castMemberGateway, times(0)).existsByIds(any());
        verify(genreGateway, times(0)).existsByIds(any());
        verify(mediaResourceGateway, times(0)).storeImage(any(), any());
        verify(mediaResourceGateway, times(0)).storeVideo(any(), any());
        verify(videoGateway, times(0)).update(any());
    }

    @Test
    void Given_AValidCommand_When_CallsUpdateVideoAndSomeCategoriesDoesNotExists_Should_ReturnDomainException() {
        final var aVideo = Fixture.Videos.random();

        final var categoryId = Fixture.Categories.random().getId();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "Some categories could not be found: %s".formatted(categoryId.getValue());

        final var expectedId = aVideo.getId();
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

        final var aCommand = UpdateVideoCommand.with(
                expectedId.getValue(),
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

        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(Video.with(aVideo)));

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

        verify(videoGateway, times(1)).findById(any());
        verify(categoryGateway, times(1)).existsByIds(any());
        verify(castMemberGateway, times(1)).existsByIds(any());
        verify(genreGateway, times(1)).existsByIds(any());
        verify(mediaResourceGateway, times(0)).storeImage(any(), any());
        verify(mediaResourceGateway, times(0)).storeVideo(any(), any());
        verify(videoGateway, times(0)).update(any());
    }

    @Test
    void Given_AValidCommand_When_CallsUpdateVideoAndSomeCastMembersDoesNotExists_Should_ReturnDomainException() {
        final var aVideo = Fixture.Videos.random();

        final var castMemberId = Fixture.CastMembers.random().getId();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "Some cast members could not be found: %s".formatted(castMemberId.getValue());

        final var expectedId = aVideo.getId();
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

        final var aCommand = UpdateVideoCommand.with(
                expectedId.getValue(),
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

        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(Video.with(aVideo)));

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

        verify(videoGateway, times(1)).findById(any());
        verify(categoryGateway, times(1)).existsByIds(any());
        verify(castMemberGateway, times(1)).existsByIds(any());
        verify(genreGateway, times(1)).existsByIds(any());
        verify(mediaResourceGateway, times(0)).storeImage(any(), any());
        verify(mediaResourceGateway, times(0)).storeVideo(any(), any());
        verify(videoGateway, times(0)).update(any());
    }

    @Test
    void Given_AValidCommand_When_CallsUpdateVideoAndSomeGenresDoesNotExists_Should_ReturnDomainException() {
        final var aVideo = Fixture.Videos.random();

        final var genreId = Fixture.Genres.random().getId();

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "Some genres could not be found: %s".formatted(genreId.getValue());

        final var expectedId = aVideo.getId();
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

        final var aCommand = UpdateVideoCommand.with(
                expectedId.getValue(),
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

        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(Video.with(aVideo)));

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

        verify(videoGateway, times(1)).findById(any());
        verify(categoryGateway, times(1)).existsByIds(any());
        verify(castMemberGateway, times(1)).existsByIds(any());
        verify(genreGateway, times(1)).existsByIds(any());
        verify(mediaResourceGateway, times(0)).storeImage(any(), any());
        verify(mediaResourceGateway, times(0)).storeVideo(any(), any());
        verify(videoGateway, times(0)).update(any());
    }

    @Test
    void Given_AValidCommand_When_CallsUpdateVideoThrowsException_Should_CallClearResources() {
        final var aVideo = Fixture.Videos.random();

        final var expectedErrorMessage = "An error on update video was observed [videoId:";

        final var expectedId = aVideo.getId();
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

        final Resource expectedBanner = Fixture.Videos.resource(VideoMediaType.BANNER);
        final Resource expectedThumbnail = Fixture.Videos.resource(VideoMediaType.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixture.Videos.resource(VideoMediaType.THUMBNAIL_HALF);
        final Resource expectedTrailer = Fixture.Videos.resource(VideoMediaType.TRAILER);
        final Resource expectedVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);

        final var aCommand = UpdateVideoCommand.with(
                expectedId.getValue(),
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

        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(Video.with(aVideo)));

        when(categoryGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCategories));

        when(castMemberGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedCastMembers));

        when(genreGateway.existsByIds(any()))
                .thenReturn(new ArrayList<>(expectedGenres));

        mockImage();

        mockVideo();

        when(videoGateway.update(any()))
                .thenThrow(new RuntimeException("Generic Error"));

        final var actualResult = assertThrows(InternalErrorException.class, () -> useCase.execute(aCommand));

        assertNotNull(actualResult);
        assertTrue(actualResult.getMessage().startsWith(expectedErrorMessage));

        verify(videoGateway, times(1)).findById(any());
        verify(categoryGateway, times(1)).existsByIds(expectedCategories);
        verify(castMemberGateway, times(1)).existsByIds(expectedCastMembers);
        verify(genreGateway, times(1)).existsByIds(expectedGenres);
        verify(mediaResourceGateway, times(3)).storeImage(any(), any());
        verify(mediaResourceGateway, times(2)).storeVideo(any(), any());
        verify(videoGateway)
                .update(argThat(actualVideo -> Objects.equals(expectedId.getValue(), actualVideo.getId().getValue())
                        && Objects.equals(expectedTitle, actualVideo.getTitle())
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
                        && Objects.equals(expectedVideo.name(), actualVideo.getVideo().get().name())
                        && Objects.equals(aVideo.getCreatedAt(), actualVideo.getCreatedAt())
                        && aVideo.getCreatedAt().isBefore(actualVideo.getUpdatedAt())));
        verify(mediaResourceGateway, times(0)).clearResources(any());
    }

    private void mockVideo() {
        when(mediaResourceGateway.storeVideo(any(), any()))
                .then(it -> {
                    final var videoResource = it.getArgument(1, VideoResource.class);
                    final var resource = videoResource.getResource();
                    final var checksum = resource.checksum();
                    final var name = resource.name();
                    return VideoMedia.with(checksum, name, "/videos");
                });
    }

    private void mockImage() {
        when(mediaResourceGateway.storeImage(any(), any()))
                .then(it -> {
                    final var videoResource = it.getArgument(1, VideoResource.class);
                    final var resource = videoResource.getResource();
                    final var checksum = resource.checksum();
                    final var name = resource.name();
                    return ImageMedia.with(checksum, name, "/images");
                });
    }

}
