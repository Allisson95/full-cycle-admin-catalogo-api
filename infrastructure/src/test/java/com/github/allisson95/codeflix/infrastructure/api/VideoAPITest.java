package com.github.allisson95.codeflix.infrastructure.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Year;
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
import com.github.allisson95.codeflix.domain.Fixture;
import com.github.allisson95.codeflix.domain.video.VideoID;

@ControllerTest(controllers = { VideoAPI.class })
class VideoAPITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateVideoUseCase createVideoUseCase;

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

}
