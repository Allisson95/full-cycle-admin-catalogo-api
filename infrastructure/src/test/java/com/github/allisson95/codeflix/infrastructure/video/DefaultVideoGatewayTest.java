package com.github.allisson95.codeflix.infrastructure.video;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Year;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.github.allisson95.codeflix.IntegrationTest;
import com.github.allisson95.codeflix.domain.Fixture;
import com.github.allisson95.codeflix.domain.castmember.CastMemberGateway;
import com.github.allisson95.codeflix.domain.castmember.CastMemberID;
import com.github.allisson95.codeflix.domain.category.CategoryGateway;
import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.domain.genre.GenreGateway;
import com.github.allisson95.codeflix.domain.genre.GenreID;
import com.github.allisson95.codeflix.domain.video.ImageMedia;
import com.github.allisson95.codeflix.domain.video.Video;
import com.github.allisson95.codeflix.domain.video.VideoMedia;
import com.github.allisson95.codeflix.infrastructure.video.persistence.VideoRepository;

@IntegrationTest
class DefaultVideoGatewayTest {

    @Autowired
    private DefaultVideoGateway videoGateway;

    @Autowired
    private CastMemberGateway castMemberGateway;

    @Autowired
    private CategoryGateway categoryGateway;

    @Autowired
    private GenreGateway genreGateway;

    @Autowired
    private VideoRepository videoRepository;

    @Test
    void testInjection() {
        assertNotNull(videoGateway);
        assertNotNull(castMemberGateway);
        assertNotNull(categoryGateway);
        assertNotNull(genreGateway);
        assertNotNull(videoRepository);
    }

    @Transactional
    @Test
    void Given_AValidVideo_When_CallsCreate_Should_PersistIt() {
        final var category = this.categoryGateway.create(Fixture.Categories.random());
        final var genre = this.genreGateway.create(Fixture.Genres.random());
        final var castMember = this.castMemberGateway.create(Fixture.CastMembers.clintEastwood());

        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchedAt = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.<CategoryID>of(category.getId());
        final var expectedGenres = Set.<GenreID>of(genre.getId());
        final var expectedCastMembers = Set.<CastMemberID>of(castMember.getId());

        final ImageMedia expectedBanner = ImageMedia.with("0bb2827c5eacf570b6064e24e0e6653b", "banner", "/images");
        final ImageMedia expectedThumbnail = ImageMedia.with("0bb2827c5eacf570b6064e24e0e6653b", "thumbnail", "/images");
        final ImageMedia expectedThumbnailHalf = ImageMedia.with("0bb2827c5eacf570b6064e24e0e6653b", "thumbnail_half", "/images");
        final VideoMedia expectedTrailer = VideoMedia.with("0bb2827c5eacf570b6064e24e0e6653b", "trailer", "/videos");
        final VideoMedia expectedVideo = VideoMedia.with("0bb2827c5eacf570b6064e24e0e6653b", "video", "/videos");

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

        final var actualVideo = this.videoGateway.create(aVideo);

        assertNotNull(actualVideo);
        assertEquals(aVideo.getId(), actualVideo.getId());
        assertEquals(expectedTitle, actualVideo.getTitle());
        assertEquals(expectedDescription, actualVideo.getDescription());
        assertEquals(expectedLaunchedAt, actualVideo.getLaunchedAt());
        assertEquals(expectedDuration, actualVideo.getDuration());
        assertEquals(expectedRating, actualVideo.getRating());
        assertEquals(expectedOpened, actualVideo.isOpened());
        assertEquals(expectedPublished, actualVideo.isPublished());
        assertEquals(expectedCategories, actualVideo.getCategories());
        assertEquals(expectedGenres, actualVideo.getGenres());
        assertEquals(expectedCastMembers, actualVideo.getCastMembers());
        assertTrue(actualVideo.getBanner().isPresent());
        assertEquals(expectedBanner.name(), actualVideo.getBanner().get().name());
        assertTrue(actualVideo.getThumbnail().isPresent());
        assertEquals(expectedThumbnail.name(), actualVideo.getThumbnail().get().name());
        assertTrue(actualVideo.getThumbnailHalf().isPresent());
        assertEquals(expectedThumbnailHalf.name(), actualVideo.getThumbnailHalf().get().name());
        assertTrue(actualVideo.getTrailer().isPresent());
        assertEquals(expectedTrailer.name(), actualVideo.getTrailer().get().name());
        assertTrue(actualVideo.getVideo().isPresent());
        assertEquals(expectedVideo.name(), actualVideo.getVideo().get().name());

        final var persistedVideo = this.videoRepository.findById(aVideo.getId().getValue()).get();

        assertEquals(aVideo.getId().getValue(), persistedVideo.getId());
        assertEquals(expectedTitle, persistedVideo.getTitle());
        assertEquals(expectedDescription, persistedVideo.getDescription());
        assertEquals(expectedLaunchedAt, Year.of(persistedVideo.getYearLaunched()));
        assertEquals(expectedDuration, persistedVideo.getDuration());
        assertEquals(expectedRating, persistedVideo.getRating());
        assertEquals(expectedOpened, persistedVideo.isOpened());
        assertEquals(expectedPublished, persistedVideo.isPublished());
        assertEquals(expectedCategories, persistedVideo.getCategoriesId());
        assertEquals(expectedGenres, persistedVideo.getGenresId());
        assertEquals(expectedCastMembers, persistedVideo.getCastMembersId());
        assertNotNull(persistedVideo.getBanner());
        assertEquals(expectedBanner.name(), persistedVideo.getBanner().getName());
        assertNotNull(persistedVideo.getThumbnail());
        assertEquals(expectedThumbnail.name(), persistedVideo.getThumbnail().getName());
        assertNotNull(persistedVideo.getThumbnailHalf());
        assertEquals(expectedThumbnailHalf.name(), persistedVideo.getThumbnailHalf().getName());
        assertNotNull(persistedVideo.getTrailer());
        assertEquals(expectedTrailer.name(), persistedVideo.getTrailer().getName());
        assertNotNull(persistedVideo.getVideo());
        assertEquals(expectedVideo.name(), persistedVideo.getVideo().getName());
    }

    @Transactional
    @Test
    void Given_AValidVideoWithoutRelations_When_CallsCreate_Should_PersistIt() {
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchedAt = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.<CategoryID>of();
        final var expectedGenres = Set.<GenreID>of();
        final var expectedCastMembers = Set.<CastMemberID>of();

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
                expectedCastMembers);

        final var actualVideo = this.videoGateway.create(aVideo);

        assertNotNull(actualVideo);
        assertEquals(aVideo.getId(), actualVideo.getId());
        assertEquals(expectedTitle, actualVideo.getTitle());
        assertEquals(expectedDescription, actualVideo.getDescription());
        assertEquals(expectedLaunchedAt, actualVideo.getLaunchedAt());
        assertEquals(expectedDuration, actualVideo.getDuration());
        assertEquals(expectedRating, actualVideo.getRating());
        assertEquals(expectedOpened, actualVideo.isOpened());
        assertEquals(expectedPublished, actualVideo.isPublished());
        assertEquals(expectedCategories, actualVideo.getCategories());
        assertEquals(expectedGenres, actualVideo.getGenres());
        assertEquals(expectedCastMembers, actualVideo.getCastMembers());
        assertTrue(actualVideo.getBanner().isEmpty());
        assertTrue(actualVideo.getThumbnail().isEmpty());
        assertTrue(actualVideo.getThumbnailHalf().isEmpty());
        assertTrue(actualVideo.getTrailer().isEmpty());
        assertTrue(actualVideo.getVideo().isEmpty());

        final var persistedVideo = this.videoRepository.findById(aVideo.getId().getValue()).get();

        assertEquals(aVideo.getId().getValue(), persistedVideo.getId());
        assertEquals(expectedTitle, persistedVideo.getTitle());
        assertEquals(expectedDescription, persistedVideo.getDescription());
        assertEquals(expectedLaunchedAt, Year.of(persistedVideo.getYearLaunched()));
        assertEquals(expectedDuration, persistedVideo.getDuration());
        assertEquals(expectedRating, persistedVideo.getRating());
        assertEquals(expectedOpened, persistedVideo.isOpened());
        assertEquals(expectedPublished, persistedVideo.isPublished());
        assertEquals(expectedCategories, persistedVideo.getCategoriesId());
        assertEquals(expectedGenres, persistedVideo.getGenresId());
        assertEquals(expectedCastMembers, persistedVideo.getCastMembersId());
        assertNull(persistedVideo.getBanner());
        assertNull(persistedVideo.getThumbnail());
        assertNull(persistedVideo.getThumbnailHalf());
        assertNull(persistedVideo.getTrailer());
        assertNull(persistedVideo.getVideo());
    }

}
