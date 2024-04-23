package com.github.allisson95.codeflix.infrastructure.video;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Year;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.github.allisson95.codeflix.IntegrationTest;
import com.github.allisson95.codeflix.domain.Fixture;
import com.github.allisson95.codeflix.domain.castmember.CastMember;
import com.github.allisson95.codeflix.domain.castmember.CastMemberGateway;
import com.github.allisson95.codeflix.domain.castmember.CastMemberID;
import com.github.allisson95.codeflix.domain.category.Category;
import com.github.allisson95.codeflix.domain.category.CategoryGateway;
import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.domain.genre.Genre;
import com.github.allisson95.codeflix.domain.genre.GenreGateway;
import com.github.allisson95.codeflix.domain.genre.GenreID;
import com.github.allisson95.codeflix.domain.video.ImageMedia;
import com.github.allisson95.codeflix.domain.video.Video;
import com.github.allisson95.codeflix.domain.video.VideoID;
import com.github.allisson95.codeflix.domain.video.VideoMedia;
import com.github.allisson95.codeflix.domain.video.VideoSearchQuery;
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

    private Category filmes;
    private Category documentarios;
    private Genre acao;
    private Genre ficcaoCientifica;
    private Genre terror;
    private CastMember nicolasCage;
    private CastMember morganFreeman;

    @BeforeEach
    public void setUp() {
        filmes = this.categoryGateway.create(Fixture.Categories.filmes());
        documentarios = this.categoryGateway.create(Fixture.Categories.documentarios());

        acao = this.genreGateway.create(Fixture.Genres.acao());
        ficcaoCientifica = this.genreGateway.create(Fixture.Genres.ficcaoCientifica());
        terror = this.genreGateway.create(Fixture.Genres.terror());

        nicolasCage = this.castMemberGateway.create(Fixture.CastMembers.nicolasCage());
        morganFreeman = this.castMemberGateway.create(Fixture.CastMembers.morganFreeman());
    }

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
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchedAt = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.<CategoryID>of(filmes.getId());
        final var expectedGenres = Set.<GenreID>of(ficcaoCientifica.getId());
        final var expectedCastMembers = Set.<CastMemberID>of(nicolasCage.getId());

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
                .updateBannerMedia(expectedBanner)
                .updateThumbnailMedia(expectedThumbnail)
                .updateThumbnailHalfMedia(expectedThumbnailHalf)
                .updateTrailerMedia(expectedTrailer)
                .updateVideoMedia(expectedVideo);

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

    @Transactional
    @Test
    void Given_AValidVideo_When_CallsUpdate_Should_PersistIt() {
        final var aVideo = this.videoGateway.create(Video.newVideo(
                Fixture.title(),
                Fixture.Videos.description(),
                Year.of(Fixture.year()),
                Fixture.duration(),
                Fixture.Videos.rating(),
                Fixture.bool(),
                Fixture.bool(),
                Set.<CategoryID>of(),
                Set.<GenreID>of(),
                Set.<CastMemberID>of()));

        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchedAt = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.<CategoryID>of(filmes.getId());
        final var expectedGenres = Set.<GenreID>of(ficcaoCientifica.getId());
        final var expectedCastMembers = Set.<CastMemberID>of(nicolasCage.getId());

        final ImageMedia expectedBanner = ImageMedia.with("0bb2827c5eacf570b6064e24e0e6653b", "banner", "/images");
        final ImageMedia expectedThumbnail = ImageMedia.with("0bb2827c5eacf570b6064e24e0e6653b", "thumbnail", "/images");
        final ImageMedia expectedThumbnailHalf = ImageMedia.with("0bb2827c5eacf570b6064e24e0e6653b", "thumbnail_half", "/images");
        final VideoMedia expectedTrailer = VideoMedia.with("0bb2827c5eacf570b6064e24e0e6653b", "trailer", "/videos");
        final VideoMedia expectedVideo = VideoMedia.with("0bb2827c5eacf570b6064e24e0e6653b", "video", "/videos");

        final var updatedVideo = Video.with(aVideo)
                .update(
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
                .updateBannerMedia(expectedBanner)
                .updateThumbnailMedia(expectedThumbnail)
                .updateThumbnailHalfMedia(expectedThumbnailHalf)
                .updateTrailerMedia(expectedTrailer)
                .updateVideoMedia(expectedVideo);

        final var actualVideo = this.videoGateway.update(updatedVideo);

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
        assertNotNull(actualVideo.getCreatedAt());
        assertNotNull(actualVideo.getUpdatedAt());
        assertTrue(actualVideo.getUpdatedAt().isAfter(actualVideo.getCreatedAt()));

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
        assertNotNull(persistedVideo.getCreatedAt());
        assertNotNull(persistedVideo.getUpdatedAt());
        assertTrue(persistedVideo.getUpdatedAt().isAfter(persistedVideo.getCreatedAt()));
    }

    @Test
    void Given_AValidVideoId_When_CallsDeleteById_Should_DeleteIt() {
        final var aVideo = this.videoGateway.create(Video.newVideo(
                Fixture.title(),
                Fixture.Videos.description(),
                Year.of(Fixture.year()),
                Fixture.duration(),
                Fixture.Videos.rating(),
                Fixture.bool(),
                Fixture.bool(),
                Set.<CategoryID>of(),
                Set.<GenreID>of(),
                Set.<CastMemberID>of()));

        final var anId = aVideo.getId();

        assertEquals(1, this.videoRepository.count());

        this.videoGateway.deleteById(anId);

        assertEquals(0, this.videoRepository.count());
    }

    @Test
    void Given_AInvalidVideoId_When_CallsDeleteById_Should_DoNothingIt() {
        this.videoGateway.create(Video.newVideo(
                Fixture.title(),
                Fixture.Videos.description(),
                Year.of(Fixture.year()),
                Fixture.duration(),
                Fixture.Videos.rating(),
                Fixture.bool(),
                Fixture.bool(),
                Set.<CategoryID>of(),
                Set.<GenreID>of(),
                Set.<CastMemberID>of()));

        final var anId = VideoID.unique();

        assertEquals(1, this.videoRepository.count());

        this.videoGateway.deleteById(anId);

        assertEquals(1, this.videoRepository.count());
    }

    @Test
    void Given_AValidVideoId_When_CallsFindById_Should_ReturnIt() {
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchedAt = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.<CategoryID>of(filmes.getId());
        final var expectedGenres = Set.<GenreID>of(ficcaoCientifica.getId());
        final var expectedCastMembers = Set.<CastMemberID>of(nicolasCage.getId());

        final ImageMedia expectedBanner = ImageMedia.with("0bb2827c5eacf570b6064e24e0e6653b", "banner", "/images");
        final ImageMedia expectedThumbnail = ImageMedia.with("0bb2827c5eacf570b6064e24e0e6653b", "thumbnail", "/images");
        final ImageMedia expectedThumbnailHalf = ImageMedia.with("0bb2827c5eacf570b6064e24e0e6653b", "thumbnail_half", "/images");
        final VideoMedia expectedTrailer = VideoMedia.with("0bb2827c5eacf570b6064e24e0e6653b", "trailer", "/videos");
        final VideoMedia expectedVideo = VideoMedia.with("0bb2827c5eacf570b6064e24e0e6653b", "video", "/videos");

        final var aVideo = this.videoGateway.create(
                Video.newVideo(
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
                        .updateBannerMedia(expectedBanner)
                        .updateThumbnailMedia(expectedThumbnail)
                        .updateThumbnailHalfMedia(expectedThumbnailHalf)
                        .updateTrailerMedia(expectedTrailer)
                        .updateVideoMedia(expectedVideo));

        final var actualVideo = this.videoGateway.findById(aVideo.getId()).get();

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
    }

    @Test
    void Given_AInvalidVideoId_When_CallsFindById_Should_ReturnEmpty() {
        this.videoGateway.create(Video.newVideo(
                Fixture.title(),
                Fixture.Videos.description(),
                Year.of(Fixture.year()),
                Fixture.duration(),
                Fixture.Videos.rating(),
                Fixture.bool(),
                Fixture.bool(),
                Set.<CategoryID>of(),
                Set.<GenreID>of(),
                Set.<CastMemberID>of()));

        final var anId = VideoID.unique();

        final var actualVideo = this.videoGateway.findById(anId);

        assertNotNull(actualVideo);
        assertTrue(actualVideo.isEmpty());
    }

    @Test
    void Given_EmptyVideos_When_CallsFindAll_Should_ReturnAEmptyList() {
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.<CastMemberID>of(),
                Set.<CategoryID>of(),
                Set.<GenreID>of());

        final var actualPage = videoGateway.findAll(aQuery);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedTotal, actualPage.items().size());
    }

    @Test
    void Given_DefaultParams_When_CallsFindAll_Should_ReturnAllList() {
        this.mockVideos();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 5;

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.<CastMemberID>of(),
                Set.<CategoryID>of(),
                Set.<GenreID>of());

        final var actualPage = this.videoGateway.findAll(aQuery);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedTotal, actualPage.items().size());
    }

    @Test
    void Given_AValidCategory_When_CallsFindAll_Should_ReturnFilteredList() {
        this.mockVideos();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 1;

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.<CastMemberID>of(),
                Set.<CategoryID>of(documentarios.getId()),
                Set.<GenreID>of());

        final var actualPage = this.videoGateway.findAll(aQuery);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedTotal, actualPage.items().size());

        assertEquals("Enigmas do Universo", actualPage.items().get(0).title());
    }

    @Test
    void Given_AValidCastMember_When_CallsFindAll_Should_ReturnFilteredList() {
        this.mockVideos();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 2;

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.<CastMemberID>of(nicolasCage.getId()),
                Set.<CategoryID>of(),
                Set.<GenreID>of());

        final var actualPage = this.videoGateway.findAll(aQuery);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedTotal, actualPage.items().size());

        assertEquals("Arcadian", actualPage.items().get(0).title());
        assertEquals("O Apocalipse", actualPage.items().get(1).title());
    }

    @Test
    void Given_AValidGenre_When_CallsFindAll_Should_ReturnFilteredList() {
        this.mockVideos();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 3;

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.<CastMemberID>of(),
                Set.<CategoryID>of(),
                Set.<GenreID>of(ficcaoCientifica.getId()));

        final var actualPage = this.videoGateway.findAll(aQuery);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedTotal, actualPage.items().size());

        assertEquals("Enigmas do Universo", actualPage.items().get(0).title());
        assertEquals("O Apocalipse", actualPage.items().get(1).title());
        assertEquals("Transcendence - A Revolução", actualPage.items().get(2).title());
    }

    @Test
    void Given_AllParams_When_CallsFindAll_Should_ReturnFilteredList() {
        this.mockVideos();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "Revolução";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedTotal = 1;

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.<CastMemberID>of(morganFreeman.getId()),
                Set.<CategoryID>of(filmes.getId()),
                Set.<GenreID>of(ficcaoCientifica.getId()));

        final var actualPage = this.videoGateway.findAll(aQuery);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedTotal, actualPage.items().size());

        assertEquals("Transcendence - A Revolução", actualPage.items().get(0).title());
    }

    @ParameterizedTest
    @CsvSource({
            "title,asc,0,10,5,5,Arcadian",
            "title,desc,0,10,5,5,Transcendence - A Revolução",
            "createdAt,asc,0,10,5,5,Arcadian",
            "createdAt,desc,0,10,5,5,O Problema dos 3 Corpos",
    })
    void Given_AValidSortAndDirection_When_CallsFindAll_Should_ReturnOrdered(
            final String expectedSort,
            final String expectedDirection,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedVideoTitle) {
        this.mockVideos();

        final var expectedTerms = "";

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.<CastMemberID>of(),
                Set.<CategoryID>of(),
                Set.<GenreID>of());

        final var actualPage = videoGateway.findAll(aQuery);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedItemsCount, actualPage.items().size());
        assertEquals(expectedVideoTitle, actualPage.items().get(0).title());
    }

    @ParameterizedTest
    @CsvSource({
            "ar,0,10,1,1,Arcadian",
            "sc,0,10,1,1,Transcendence - A Revolução",
            "pro,0,10,1,1,O Problema dos 3 Corpos",
            "vers,0,10,1,1,Enigmas do Universo",
            "apoca,0,10,1,1,O Apocalipse",
    })
    void Given_AValidTerms_When_CallsFindAll_Should_ReturnFiltered(
            final String expectedTerms,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedVideoTitle) {
        this.mockVideos();

        final var expectedSort = "title";
        final var expectedDirection = "asc";

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.<CastMemberID>of(),
                Set.<CategoryID>of(),
                Set.<GenreID>of());

        final var actualPage = this.videoGateway.findAll(aQuery);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedItemsCount, actualPage.items().size());
        assertEquals(expectedVideoTitle, actualPage.items().get(0).title());
    }

    @ParameterizedTest
    @CsvSource({
            "0,2,2,5,Arcadian;Enigmas do Universo",
            "1,2,2,5,O Apocalipse;O Problema dos 3 Corpos",
            "2,2,1,5,Transcendence - A Revolução",
    })
    void Given_AValidPage_When_CallsFindAll_Should_ReturnPaginated(
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedVideos) {
        this.mockVideos();

        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";

        final var aQuery = new VideoSearchQuery(
                expectedPage,
                expectedPerPage,
                expectedTerms,
                expectedSort,
                expectedDirection,
                Set.<CastMemberID>of(),
                Set.<CategoryID>of(),
                Set.<GenreID>of());

        final var actualPage = this.videoGateway.findAll(aQuery);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedItemsCount, actualPage.items().size());

        final var expectedVideosTitle = expectedVideos.split(";");
        for (int i = 0; i < expectedVideosTitle.length; i++) {
            assertEquals(expectedVideosTitle[i], actualPage.items().get(i).title());
        }
    }

    private void mockVideos() {
        this.videoGateway.create(Video.newVideo(
                "Arcadian",
                Fixture.Videos.description(),
                Year.of(Fixture.year()),
                Fixture.duration(),
                Fixture.Videos.rating(),
                Fixture.bool(),
                Fixture.bool(),
                Set.<CategoryID>of(filmes.getId()),
                Set.<GenreID>of(terror.getId()),
                Set.<CastMemberID>of(nicolasCage.getId())));

        this.videoGateway.create(Video.newVideo(
                "O Apocalipse",
                Fixture.Videos.description(),
                Year.of(Fixture.year()),
                Fixture.duration(),
                Fixture.Videos.rating(),
                Fixture.bool(),
                Fixture.bool(),
                Set.<CategoryID>of(filmes.getId()),
                Set.<GenreID>of(acao.getId(), ficcaoCientifica.getId()),
                Set.<CastMemberID>of(nicolasCage.getId())));

        this.videoGateway.create(Video.newVideo(
                "Transcendence - A Revolução",
                Fixture.Videos.description(),
                Year.of(Fixture.year()),
                Fixture.duration(),
                Fixture.Videos.rating(),
                Fixture.bool(),
                Fixture.bool(),
                Set.<CategoryID>of(filmes.getId()),
                Set.<GenreID>of(ficcaoCientifica.getId()),
                Set.<CastMemberID>of(morganFreeman.getId())));

        this.videoGateway.create(Video.newVideo(
                "Enigmas do Universo",
                Fixture.Videos.description(),
                Year.of(Fixture.year()),
                Fixture.duration(),
                Fixture.Videos.rating(),
                Fixture.bool(),
                Fixture.bool(),
                Set.<CategoryID>of(documentarios.getId()),
                Set.<GenreID>of(ficcaoCientifica.getId()),
                Set.<CastMemberID>of(morganFreeman.getId())));

        this.videoGateway.create(Video.newVideo(
                "O Problema dos 3 Corpos",
                Fixture.Videos.description(),
                Year.of(Fixture.year()),
                Fixture.duration(),
                Fixture.Videos.rating(),
                Fixture.bool(),
                Fixture.bool(),
                Set.<CategoryID>of(),
                Set.<GenreID>of(),
                Set.<CastMemberID>of()));
    }

}
