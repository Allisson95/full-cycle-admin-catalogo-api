package com.github.allisson95.codeflix.infrastructure.api;

import static com.github.allisson95.codeflix.domain.utils.CollectionUtils.mapTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_LENGTH;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.allisson95.codeflix.ControllerTest;
import com.github.allisson95.codeflix.application.video.create.CreateVideoCommand;
import com.github.allisson95.codeflix.application.video.create.CreateVideoOutput;
import com.github.allisson95.codeflix.application.video.create.CreateVideoUseCase;
import com.github.allisson95.codeflix.application.video.delete.DeleteVideoUseCase;
import com.github.allisson95.codeflix.application.video.media.get.GetMediaCommand;
import com.github.allisson95.codeflix.application.video.media.get.GetMediaUseCase;
import com.github.allisson95.codeflix.application.video.media.get.MediaOutput;
import com.github.allisson95.codeflix.application.video.retrieve.get.GetVideoByIdUseCase;
import com.github.allisson95.codeflix.application.video.retrieve.get.VideoOutput;
import com.github.allisson95.codeflix.application.video.retrieve.list.ListVideoUseCase;
import com.github.allisson95.codeflix.application.video.retrieve.list.VideoListOutput;
import com.github.allisson95.codeflix.application.video.update.UpdateVideoCommand;
import com.github.allisson95.codeflix.application.video.update.UpdateVideoOutput;
import com.github.allisson95.codeflix.application.video.update.UpdateVideoUseCase;
import com.github.allisson95.codeflix.domain.Fixture;
import com.github.allisson95.codeflix.domain.castmember.CastMemberID;
import com.github.allisson95.codeflix.domain.category.CategoryID;
import com.github.allisson95.codeflix.domain.exceptions.NotificationException;
import com.github.allisson95.codeflix.domain.genre.GenreID;
import com.github.allisson95.codeflix.domain.pagination.Pagination;
import com.github.allisson95.codeflix.domain.validation.handler.Notification;
import com.github.allisson95.codeflix.domain.video.Video;
import com.github.allisson95.codeflix.domain.video.VideoID;
import com.github.allisson95.codeflix.domain.video.VideoMediaType;
import com.github.allisson95.codeflix.domain.video.VideoPreview;
import com.github.allisson95.codeflix.domain.video.VideoSearchQuery;
import com.github.allisson95.codeflix.infrastructure.video.models.CreateVideoRequest;
import com.github.allisson95.codeflix.infrastructure.video.models.UpdateVideoRequest;

@ControllerTest(controllers = { VideoAPI.class })
class VideoAPITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateVideoUseCase createVideoUseCase;

    @MockBean
    private GetVideoByIdUseCase getVideoByIdUseCase;

    @MockBean
    private UpdateVideoUseCase updateVideoUseCase;

    @MockBean
    private DeleteVideoUseCase deleteVideoUseCase;

    @MockBean
    private ListVideoUseCase listVideoUseCase;

    @MockBean
    private GetMediaUseCase getMediaUseCase;

    @Test
    void Given_AllParams_When_CallsCreateFull_Then_ReturnId() throws Exception {
        // given
        final var category = Fixture.Categories.random();
        final var genre = Fixture.Genres.random();
        final var member = Fixture.CastMembers.clintEastwood();

        final var expectedId = VideoID.unique();
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchedAt = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.of(category.getId().getValue());
        final var expectedGenres = Set.of(genre.getId().getValue());
        final var expectedCastMembers = Set.of(member.getId().getValue());

        final var expectedBanner = new MockMultipartFile(
                "banner_file",
                "banner.jpg",
                "image/jpg",
                "BANNER".getBytes());
        final var expectedThumbnail = new MockMultipartFile(
                "thumb_file",
                "thumbnail.jpg",
                "image/jpg",
                "THUMBNAIL".getBytes());
        final var expectedThumbnailHalf = new MockMultipartFile(
                "thumb_half_file",
                "thumbnail_half.jpg",
                "image/jpg",
                "THUMBNAIL_HALF".getBytes());
        final var expectedTrailer = new MockMultipartFile(
                "trailer_file",
                "trailer.mp4",
                "video/mp4",
                "TRAILER".getBytes());
        final var expectedVideo = new MockMultipartFile(
                "video_file",
                "video.mp4",
                "video/mp4",
                "VIDEO".getBytes());

        when(createVideoUseCase.execute(any()))
                .thenReturn(new CreateVideoOutput(expectedId.getValue()));

        // when
        final var request = multipart("/videos")
                .file(expectedBanner)
                .file(expectedThumbnail)
                .file(expectedThumbnailHalf)
                .file(expectedTrailer)
                .file(expectedVideo)
                .param("title", expectedTitle)
                .param("description", expectedDescription)
                .param("year_launched", String.valueOf(expectedLaunchedAt.getValue()))
                .param("duration", String.valueOf(expectedDuration))
                .param("rating", expectedRating.getName())
                .param("opened", String.valueOf(expectedOpened))
                .param("published", String.valueOf(expectedPublished))
                .param("categories_id", category.getId().getValue())
                .param("genres_id", genre.getId().getValue())
                .param("cast_members_id", member.getId().getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", equalTo("/videos/" + expectedId.getValue())))
                .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())));
        // then
        final var captor = ArgumentCaptor.forClass(CreateVideoCommand.class);

        verify(createVideoUseCase).execute(captor.capture());

        final var aCommand = captor.getValue();

        assertEquals(expectedTitle, aCommand.title());
        assertEquals(expectedDescription, aCommand.description());
        assertEquals(expectedLaunchedAt.getValue(), aCommand.launchedAt());
        assertEquals(expectedDuration, aCommand.duration());
        assertEquals(expectedRating.getName(), aCommand.rating());
        assertEquals(expectedOpened, aCommand.opened());
        assertEquals(expectedPublished, aCommand.published());
        assertEquals(expectedCategories, aCommand.categories());
        assertEquals(expectedGenres, aCommand.genres());
        assertEquals(expectedCastMembers, aCommand.castMembers());
        assertThat(aCommand.getBanner())
                .isPresent()
                .hasValueSatisfying(resource -> {
                    assertEquals(expectedBanner.getOriginalFilename(), resource.name());
                });
        assertThat(aCommand.getThumbnail())
                .isPresent()
                .hasValueSatisfying(resource -> {
                    assertEquals(expectedThumbnail.getOriginalFilename(), resource.name());
                });
        assertThat(aCommand.getThumbnailHalf())
                .isPresent()
                .hasValueSatisfying(resource -> {
                    assertEquals(expectedThumbnailHalf.getOriginalFilename(), resource.name());
                });
        assertThat(aCommand.getTrailer())
                .isPresent()
                .hasValueSatisfying(resource -> {
                    assertEquals(expectedTrailer.getOriginalFilename(), resource.name());
                });
        assertThat(aCommand.getVideo())
                .isPresent()
                .hasValueSatisfying(resource -> {
                    assertEquals(expectedVideo.getOriginalFilename(), resource.name());
                });
    }

    @Test
    void Given_AValidCommand_When_CallsCreatePartial_Should_ReturnId() throws Exception {
        // given
        final var category = Fixture.Categories.random();
        final var genre = Fixture.Genres.random();
        final var member = Fixture.CastMembers.clintEastwood();

        final var expectedId = VideoID.unique();
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchedAt = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.of(category.getId().getValue());
        final var expectedGenres = Set.of(genre.getId().getValue());
        final var expectedCastMembers = Set.of(member.getId().getValue());

        final var createVideoRequest = new CreateVideoRequest(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt.getValue(),
                expectedDuration,
                expectedRating.getName(),
                expectedOpened,
                expectedPublished,
                expectedCategories,
                expectedGenres,
                expectedCastMembers);

        when(createVideoUseCase.execute(any()))
                .thenReturn(new CreateVideoOutput(expectedId.getValue()));
        // when
        final var request = post("/videos")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsBytes(createVideoRequest));

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", equalTo("/videos/" + expectedId.getValue())))
                .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())));

        // then
        final var captor = ArgumentCaptor.forClass(CreateVideoCommand.class);

        verify(createVideoUseCase).execute(captor.capture());

        final var aCommand = captor.getValue();

        assertEquals(expectedTitle, aCommand.title());
        assertEquals(expectedDescription, aCommand.description());
        assertEquals(expectedLaunchedAt.getValue(), aCommand.launchedAt());
        assertEquals(expectedDuration, aCommand.duration());
        assertEquals(expectedRating.getName(), aCommand.rating());
        assertEquals(expectedOpened, aCommand.opened());
        assertEquals(expectedPublished, aCommand.published());
        assertEquals(expectedCategories, aCommand.categories());
        assertEquals(expectedGenres, aCommand.genres());
        assertEquals(expectedCastMembers, aCommand.castMembers());
        assertThat(aCommand.getBanner()).isEmpty();
        assertThat(aCommand.getThumbnail()).isEmpty();
        assertThat(aCommand.getThumbnailHalf()).isEmpty();
        assertThat(aCommand.getTrailer()).isEmpty();
        assertThat(aCommand.getVideo()).isEmpty();
    }

    @Test
    void Given_AValidId_When_CallsGetById_Then_ReturnVideo() throws Exception {
        // given
        final var category = Fixture.Categories.random();
        final var genre = Fixture.Genres.random();
        final var member = Fixture.CastMembers.clintEastwood();

        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchedAt = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.of(category.getId().getValue());
        final var expectedGenres = Set.of(genre.getId().getValue());
        final var expectedCastMembers = Set.of(member.getId().getValue());

        final var expectedBanner = Fixture.Videos.imageMedia(VideoMediaType.BANNER);
        final var expectedThumbnail = Fixture.Videos.imageMedia(VideoMediaType.THUMBNAIL);
        final var expectedThumbnailHalf = Fixture.Videos.imageMedia(VideoMediaType.THUMBNAIL_HALF);

        final var expectedTrailer = Fixture.Videos.videoMedia(VideoMediaType.TRAILER);
        final var expectedVideo = Fixture.Videos.videoMedia(VideoMediaType.VIDEO);

        final var aVideo = Video.newVideo(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt,
                expectedDuration,
                expectedRating,
                expectedOpened,
                expectedPublished,
                mapTo(expectedCategories, CategoryID::from),
                mapTo(expectedGenres, GenreID::from),
                mapTo(expectedCastMembers, CastMemberID::from))
                .updateBannerMedia(expectedBanner)
                .updateThumbnailMedia(expectedThumbnail)
                .updateThumbnailHalfMedia(expectedThumbnailHalf)
                .updateTrailerMedia(expectedTrailer)
                .updateVideoMedia(expectedVideo);

        final var expectedId = aVideo.getId().getValue();

        when(getVideoByIdUseCase.execute(any()))
                .thenReturn(VideoOutput.from(aVideo));

        // when
        final var request = get("/videos/{videoId}", expectedId)
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mockMvc.perform(request).andDo(print());

        // then
        response
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", equalTo(expectedId)))
                .andExpect(jsonPath("$.title", equalTo(expectedTitle)))
                .andExpect(jsonPath("$.description", equalTo(expectedDescription)))
                .andExpect(jsonPath("$.year_launched", equalTo(expectedLaunchedAt.getValue())))
                .andExpect(jsonPath("$.duration", equalTo(expectedDuration)))
                .andExpect(jsonPath("$.rating", equalTo(expectedRating.getName())))
                .andExpect(jsonPath("$.opened", equalTo(expectedOpened)))
                .andExpect(jsonPath("$.published", equalTo(expectedPublished)))
                .andExpect(jsonPath("$.created_at", equalTo(aVideo.getCreatedAt().toString())))
                .andExpect(jsonPath("$.updated_at", equalTo(aVideo.getUpdatedAt().toString())))

                .andExpect(jsonPath("$.banner.id", equalTo(expectedBanner.id())))
                .andExpect(jsonPath("$.banner.name", equalTo(expectedBanner.name())))
                .andExpect(jsonPath("$.banner.location", equalTo(expectedBanner.location())))
                .andExpect(jsonPath("$.banner.checksum", equalTo(expectedBanner.checksum())))

                .andExpect(jsonPath("$.thumbnail.id", equalTo(expectedThumbnail.id())))
                .andExpect(jsonPath("$.thumbnail.name", equalTo(expectedThumbnail.name())))
                .andExpect(jsonPath("$.thumbnail.location", equalTo(expectedThumbnail.location())))
                .andExpect(jsonPath("$.thumbnail.checksum", equalTo(expectedThumbnail.checksum())))

                .andExpect(jsonPath("$.thumbnail_half.id", equalTo(expectedThumbnailHalf.id())))
                .andExpect(jsonPath("$.thumbnail_half.name", equalTo(expectedThumbnailHalf.name())))
                .andExpect(jsonPath("$.thumbnail_half.location", equalTo(expectedThumbnailHalf.location())))
                .andExpect(jsonPath("$.thumbnail_half.checksum", equalTo(expectedThumbnailHalf.checksum())))

                .andExpect(jsonPath("$.trailer.id", equalTo(expectedTrailer.id())))
                .andExpect(jsonPath("$.trailer.name", equalTo(expectedTrailer.name())))
                .andExpect(jsonPath("$.trailer.checksum", equalTo(expectedTrailer.checksum())))
                .andExpect(jsonPath("$.trailer.location", equalTo(expectedTrailer.rawLocation())))
                .andExpect(jsonPath("$.trailer.encoded_location", equalTo(expectedTrailer.encodedLocation())))
                .andExpect(jsonPath("$.trailer.status", equalTo(expectedTrailer.status().name())))

                .andExpect(jsonPath("$.video.id", equalTo(expectedVideo.id())))
                .andExpect(jsonPath("$.video.name", equalTo(expectedVideo.name())))
                .andExpect(jsonPath("$.video.checksum", equalTo(expectedVideo.checksum())))
                .andExpect(jsonPath("$.video.location", equalTo(expectedVideo.rawLocation())))
                .andExpect(jsonPath("$.video.encoded_location", equalTo(expectedVideo.encodedLocation())))
                .andExpect(jsonPath("$.video.status", equalTo(expectedVideo.status().name())))

                .andExpect(jsonPath("$.categories_id", equalTo(new ArrayList<>(expectedCategories))))
                .andExpect(jsonPath("$.genres_id", equalTo(new ArrayList<>(expectedGenres))))
                .andExpect(jsonPath("$.cast_members_id", equalTo(new ArrayList<>(expectedCastMembers))));
    }

    @Test
    void Given_AValidCommand_When_CallsUpdateVideo_Then_ReturnVideoId() throws Exception {
        // given
        final var category = Fixture.Categories.random();
        final var genre = Fixture.Genres.random();
        final var member = Fixture.CastMembers.clintEastwood();

        final var expectedId = VideoID.unique();
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchedAt = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.of(category.getId().getValue());
        final var expectedGenres = Set.of(genre.getId().getValue());
        final var expectedCastMembers = Set.of(member.getId().getValue());

        final var updateVideoRequest = new UpdateVideoRequest(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt.getValue(),
                expectedDuration,
                expectedRating.getName(),
                expectedOpened,
                expectedPublished,
                expectedCategories,
                expectedGenres,
                expectedCastMembers);

        when(updateVideoUseCase.execute(any()))
                .thenReturn(new UpdateVideoOutput(expectedId.getValue()));

        // when
        final var request = put("/videos/{id}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsBytes(updateVideoRequest));

        this.mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())));

        // then
        final var captor = ArgumentCaptor.forClass(UpdateVideoCommand.class);

        verify(updateVideoUseCase).execute(captor.capture());

        final var aCommand = captor.getValue();

        assertEquals(expectedTitle, aCommand.title());
        assertEquals(expectedDescription, aCommand.description());
        assertEquals(expectedLaunchedAt.getValue(), aCommand.launchedAt());
        assertEquals(expectedDuration, aCommand.duration());
        assertEquals(expectedRating.getName(), aCommand.rating());
        assertEquals(expectedOpened, aCommand.opened());
        assertEquals(expectedPublished, aCommand.published());
        assertEquals(expectedCategories, aCommand.categories());
        assertEquals(expectedGenres, aCommand.genres());
        assertEquals(expectedCastMembers, aCommand.castMembers());
        assertThat(aCommand.getBanner()).isEmpty();
        assertThat(aCommand.getThumbnail()).isEmpty();
        assertThat(aCommand.getThumbnailHalf()).isEmpty();
        assertThat(aCommand.getTrailer()).isEmpty();
        assertThat(aCommand.getVideo()).isEmpty();
    }

    @Test
    void Given_AInvalidCommand_When_CallsUpdateVideo_Then_ReturnNotification() throws Exception {
        // given
        final var category = Fixture.Categories.random();
        final var genre = Fixture.Genres.random();
        final var member = Fixture.CastMembers.clintEastwood();

        final var expectedId = VideoID.unique();
        final var expectedTitle = " ";
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchedAt = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedCategories = Set.of(category.getId().getValue());
        final var expectedGenres = Set.of(genre.getId().getValue());
        final var expectedCastMembers = Set.of(member.getId().getValue());

        final var expectedErrorMessage = "'title' should not be null";
        final var expectedErrorCount = 1;

        final var updateVideoRequest = new UpdateVideoRequest(
                expectedTitle,
                expectedDescription,
                expectedLaunchedAt.getValue(),
                expectedDuration,
                expectedRating.getName(),
                expectedOpened,
                expectedPublished,
                expectedCategories,
                expectedGenres,
                expectedCastMembers);

        when(updateVideoUseCase.execute(any()))
                .thenThrow(
                        new NotificationException("Error", Notification.create(new Error(expectedErrorMessage))));

        // when
        final var request = put("/videos/{id}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsBytes(updateVideoRequest));

        final var response = this.mockMvc.perform(request).andDo(print());

        // then
        response
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(expectedErrorCount)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        final var captor = ArgumentCaptor.forClass(UpdateVideoCommand.class);

        verify(updateVideoUseCase).execute(captor.capture());

        final var aCommand = captor.getValue();

        assertEquals(expectedTitle, aCommand.title());
        assertEquals(expectedDescription, aCommand.description());
        assertEquals(expectedLaunchedAt.getValue(), aCommand.launchedAt());
        assertEquals(expectedDuration, aCommand.duration());
        assertEquals(expectedRating.getName(), aCommand.rating());
        assertEquals(expectedOpened, aCommand.opened());
        assertEquals(expectedPublished, aCommand.published());
        assertEquals(expectedCategories, aCommand.categories());
        assertEquals(expectedGenres, aCommand.genres());
        assertEquals(expectedCastMembers, aCommand.castMembers());
        assertThat(aCommand.getBanner()).isEmpty();
        assertThat(aCommand.getThumbnail()).isEmpty();
        assertThat(aCommand.getThumbnailHalf()).isEmpty();
        assertThat(aCommand.getTrailer()).isEmpty();
        assertThat(aCommand.getVideo()).isEmpty();
    }

    @Test
    void Given_AValidId_When_CallsDeleteById_Then_DeleteIt() throws Exception {
        // given
        final var expectedId = VideoID.unique();

        doNothing()
                .when(deleteVideoUseCase).execute(any());

        // when
        final var request = delete("/videos/{videoId}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mockMvc.perform(request).andDo(print());

        // then
        response.andExpect(status().isNoContent());

        verify(deleteVideoUseCase).execute(expectedId.getValue());
    }

    @Test
    void Given_AValidParams_When_CallsListVideos_Then_ReturnVideosPaginated() throws Exception {
        // given
        final var aVideo = new VideoPreview(Fixture.Videos.random());

        final var expectedPage = 5;
        final var expectedPerPage = 10;
        final var expectedTerms = "Algo";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedCastMembers = "cast1";
        final var expectedGenres = "gen1";
        final var expectedCategories = "cat1";

        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        final var expectedItems = List.of(VideoListOutput.from(aVideo));

        when(listVideoUseCase.execute(any()))
                .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

        // when
        final var request = get("/videos")
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectedPerPage))
                .queryParam("sort", expectedSort)
                .queryParam("dir", expectedDirection)
                .queryParam("search", expectedTerms)
                .queryParam("cast_members_ids", expectedCastMembers)
                .queryParam("categories_ids", expectedCategories)
                .queryParam("genres_ids", expectedGenres)
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mockMvc.perform(request).andDo(print());
        // then

        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                .andExpect(jsonPath("$.items[0].id", equalTo(aVideo.id())))
                .andExpect(jsonPath("$.items[0].title", equalTo(aVideo.title())))
                .andExpect(jsonPath("$.items[0].description", equalTo(aVideo.description())))
                .andExpect(jsonPath("$.items[0].created_at", equalTo(aVideo.createdAt().toString())))
                .andExpect(jsonPath("$.items[0].updated_at", equalTo(aVideo.updatedAt().toString())));

        final var captor = ArgumentCaptor.forClass(VideoSearchQuery.class);

        verify(listVideoUseCase).execute(captor.capture());

        final var actualQuery = captor.getValue();
        assertEquals(expectedPage, actualQuery.page());
        assertEquals(expectedPerPage, actualQuery.perPage());
        assertEquals(expectedTerms, actualQuery.terms());
        assertEquals(expectedSort, actualQuery.sort());
        assertEquals(expectedDirection, actualQuery.direction());
        assertEquals(Set.of(CastMemberID.from(expectedCastMembers)), actualQuery.castMembers());
        assertEquals(Set.of(GenreID.from(expectedGenres)), actualQuery.genres());
        assertEquals(Set.of(CategoryID.from(expectedCategories)), actualQuery.categories());
    }

    @Test
    void Given_AEmptyParams_When_CallsListVideosWithDefault_Then_ReturnVideosPaginated() throws Exception {
        // given
        final var aVideo = new VideoPreview(Fixture.Videos.random());

        final var expectedPage = 0;
        final var expectedPerPage = 25;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";

        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        final var expectedItems = List.of(VideoListOutput.from(aVideo));

        when(listVideoUseCase.execute(any()))
                .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

        // when
        final var request = get("/videos")
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mockMvc.perform(request).andDo(print());
        // then

        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                .andExpect(jsonPath("$.items[0].id", equalTo(aVideo.id())))
                .andExpect(jsonPath("$.items[0].title", equalTo(aVideo.title())))
                .andExpect(jsonPath("$.items[0].description", equalTo(aVideo.description())))
                .andExpect(jsonPath("$.items[0].created_at", equalTo(aVideo.createdAt().toString())))
                .andExpect(jsonPath("$.items[0].updated_at", equalTo(aVideo.updatedAt().toString())));

        final var captor = ArgumentCaptor.forClass(VideoSearchQuery.class);

        verify(listVideoUseCase).execute(captor.capture());

        final var actualQuery = captor.getValue();
        assertEquals(expectedPage, actualQuery.page());
        assertEquals(expectedPerPage, actualQuery.perPage());
        assertEquals(expectedTerms, actualQuery.terms());
        assertEquals(expectedSort, actualQuery.sort());
        assertEquals(expectedDirection, actualQuery.direction());
        assertThat(actualQuery.castMembers()).isEmpty();
        assertThat(actualQuery.genres()).isEmpty();
        assertThat(actualQuery.categories()).isEmpty();
    }

    @Test
    void Given_AValidVideoIdAndFileType_When_CallsGetMediaById_Then_ReturnContent() throws Exception {
        // given
        final var expectedId = VideoID.unique();

        final var expectedMediaType = VideoMediaType.VIDEO;
        final var expectedResource = Fixture.Videos.resource(expectedMediaType);

        final var expectedMedia = new MediaOutput(
                expectedResource.content(),
                expectedResource.contentType(),
                expectedResource.name());

        when(getMediaUseCase.execute(any())).thenReturn(expectedMedia);

        // when
        final var request = get("/videos/{id}/medias/{type}", expectedId.getValue(), expectedMediaType.name());

        final var response = this.mockMvc.perform(request).andDo(print());

        // then
        response
                .andExpect(status().isOk())
                .andExpect(header().string(CONTENT_TYPE, expectedMedia.contentType()))
                .andExpect(header().string(CONTENT_LENGTH, String.valueOf(expectedMedia.content().length)))
                .andExpect(
                        header().string(CONTENT_DISPOSITION, "attachment; filename=%s".formatted(expectedMedia.name())))
                .andExpect(content().bytes(expectedMedia.content()));

        final var captor = ArgumentCaptor.forClass(GetMediaCommand.class);

        verify(getMediaUseCase).execute(captor.capture());

        final var command = captor.getValue();

        assertEquals(expectedId.getValue(), command.videoId());
        assertEquals(expectedMediaType.name(), command.mediaType());
    }

}
