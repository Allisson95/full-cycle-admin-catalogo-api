package com.github.allisson95.codeflix.application.video.update;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.github.allisson95.codeflix.application.Fixture;
import com.github.allisson95.codeflix.application.UseCaseTest;
import com.github.allisson95.codeflix.domain.castmember.CastMemberGateway;
import com.github.allisson95.codeflix.domain.castmember.CastMemberID;
import com.github.allisson95.codeflix.domain.category.CategoryGateway;
import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.domain.genre.GenreGateway;
import com.github.allisson95.codeflix.domain.genre.GenreID;
import com.github.allisson95.codeflix.domain.video.ImageMedia;
import com.github.allisson95.codeflix.domain.video.MediaResourceGateway;
import com.github.allisson95.codeflix.domain.video.MediaStatus;
import com.github.allisson95.codeflix.domain.video.Resource;
import com.github.allisson95.codeflix.domain.video.Video;
import com.github.allisson95.codeflix.domain.video.VideoGateway;
import com.github.allisson95.codeflix.domain.video.VideoMedia;

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

        final Resource expectedBanner = Fixture.Videos.resource(Resource.Type.BANNER);
        final Resource expectedThumbnail = Fixture.Videos.resource(Resource.Type.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixture.Videos.resource(Resource.Type.THUMBNAIL_HALF);
        final Resource expectedTrailer = Fixture.Videos.resource(Resource.Type.TRAILER);
        final Resource expectedVideo = Fixture.Videos.resource(Resource.Type.VIDEO);

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

        when(mediaResourceGateway.storeImage(any(), any()))
                .then(it -> {
                    final var aResource = it.getArgument(1, Resource.class);
                    return ImageMedia.with(UUID.randomUUID().toString(), aResource.name(), "/images");
                });

        when(mediaResourceGateway.storeVideo(any(), any()))
                .then(it -> {
                    final var aResource = it.getArgument(1, Resource.class);
                    return VideoMedia.with(UUID.randomUUID().toString(), aResource.name(), "/videos", "/encoded", MediaStatus.PENDING);
                });

        when(videoGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var actualResult = useCase.execute(aCommand);

        assertNotNull(actualResult);
        assertNotNull(actualResult.id());

        verify(videoGateway).findById(expectedId);
        verify(videoGateway).update(argThat(actualVideo -> 
                Objects.equals(expectedId.getValue(), actualVideo.getId().getValue())
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

}
