package com.github.allisson95.codeflix.application.video.create;

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
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
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
import com.github.allisson95.codeflix.domain.video.Video;
import com.github.allisson95.codeflix.domain.video.VideoGateway;

class CreateVideoUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultCreateVideoUseCase useCase;

    @Mock
    private VideoGateway videoGateway;

    @Mock
    private CategoryGateway categoryGateway;

    @Mock
    private CastMemberGateway castMemberGateway;

    @Mock
    private GenreGateway genreGateway;

    @Override
    protected List<Object> getMocksForClean() {
        throw new UnsupportedOperationException("Unimplemented method 'getMocksForClean'");
    }

    @Test
    void Given_AValidCommand_When_CallsCreateVideo_Should_ReturnVideoId() {
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchedYear = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.<CategoryID>of(Fixture.Categories.random().getId());
        final var expectedGenres = Set.<GenreID>of(Fixture.Genres.random().getId());
        final var expectedCastMembers = Set.<CastMemberID>of(
                Fixture.CastMembers.clintEastwood().getId(),
                Fixture.CastMembers.morganFreeman().getId());

        final Resource expectedBanner = Fixture.Videos.resource(Type.BANNER);
        final Resource expectedThumbnail = Fixture.Videos.resource(Type.THUMBNAIL);
        final Resource expectedThumbnailHalf = Fixture.Videos.resource(Type.THUMBNAIL_HALF);
        final Resource expectedTrailer = Fixture.Videos.resource(Type.TRAILER);
        final Resource expectedVideo = Fixture.Videos.resource(Type.VIDEO);

        final var aCommand = CreateVideoCommand.with(
                expectedTitle,
                expectedDescription,
                expectedLaunchedYear,
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
                && Objects.equals(expectedLaunchedYear, actualVideo.getLaunchedAt())
                && Objects.equals(expectedDuration, actualVideo.getDuration())
                && Objects.equals(expectedRating, actualVideo.getRating())
                && Objects.equals(expectedOpened, actualVideo.isOpened())
                && Objects.equals(expectedPublished, actualVideo.isPublished())
                && Objects.equals(expectedCategories, actualVideo.getCategories())
                && Objects.equals(expectedGenres, actualVideo.getGenres())
                && Objects.equals(expectedCastMembers, actualVideo.getCastMembers())
                && actualVideo.getBanner().isPresent()
                && actualVideo.getThumbnail().isPresent()
                && actualVideo.getThumbnailHalf().isPresent()
                && actualVideo.getTrailer().isPresent()
                && actualVideo.getVideo().isPresent()));
    }

}
