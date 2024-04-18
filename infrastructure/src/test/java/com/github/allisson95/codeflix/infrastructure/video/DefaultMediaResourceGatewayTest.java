package com.github.allisson95.codeflix.infrastructure.video;

import static com.github.allisson95.codeflix.domain.Fixture.Videos.resource;
import static com.github.allisson95.codeflix.domain.Fixture.Videos.videoMediaType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.allisson95.codeflix.IntegrationTest;
import com.github.allisson95.codeflix.domain.Fixture;
import com.github.allisson95.codeflix.domain.video.MediaResourceGateway;
import com.github.allisson95.codeflix.domain.video.MediaStatus;
import com.github.allisson95.codeflix.domain.video.VideoID;
import com.github.allisson95.codeflix.domain.video.VideoMediaType;
import com.github.allisson95.codeflix.domain.video.VideoResource;
import com.github.allisson95.codeflix.infrastructure.services.StorageService;
import com.github.allisson95.codeflix.infrastructure.services.local.InMemoryStorageService;

@IntegrationTest
class DefaultMediaResourceGatewayTest {

    @Autowired
    private MediaResourceGateway mediaResourceGateway;

    @Autowired
    private StorageService storageService;

    @BeforeEach
    void setUp() {
        this.storageService().reset();
    }

    @Test
    void testInjection() {
        assertNotNull(mediaResourceGateway);
        assertInstanceOf(DefaultMediaResourceGateway.class, mediaResourceGateway);

        assertNotNull(storageService);
        assertInstanceOf(InMemoryStorageService.class, storageService);
    }

    @Test
    void Given_AValidResource_When_CallsStorageVideo_Should_StoreIt() {
        final var expectedVideoId = VideoID.unique();
        final var expectedType = VideoMediaType.VIDEO;
        final var expectedResource = Fixture.Videos.resource(expectedType);
        final var expectedLocation = "videoId-%s/type-%s".formatted(expectedVideoId.getValue(), expectedType.name());
        final var expectedStatus = MediaStatus.PENDING;
        final var expectedEncodedLocation = "";

        final var actualMedia = this.mediaResourceGateway.storeVideo(
                expectedVideoId,
                VideoResource.with(expectedResource, expectedType));

        assertNotNull(actualMedia.id());
        assertEquals(expectedLocation, actualMedia.rawLocation());
        assertEquals(expectedResource.checksum(), actualMedia.checksum());
        assertEquals(expectedResource.name(), actualMedia.name());
        assertEquals(expectedStatus, actualMedia.status());
        assertEquals(expectedEncodedLocation, actualMedia.encodedLocation());

        final var actualStored = storageService().storage().get(expectedLocation);

        assertEquals(expectedResource, actualStored);
    }

    @Test
    void Given_AValidResource_When_CallsStorageImage_Should_StoreIt() {
        final var expectedVideoId = VideoID.unique();
        final var expectedType = VideoMediaType.BANNER;
        final var expectedResource = Fixture.Videos.resource(expectedType);
        final var expectedLocation = "videoId-%s/type-%s".formatted(expectedVideoId.getValue(), expectedType.name());

        final var actualMedia = this.mediaResourceGateway.storeImage(
                expectedVideoId,
                VideoResource.with(expectedResource, expectedType));

        assertNotNull(actualMedia.id());
        assertEquals(expectedLocation, actualMedia.location());
        assertEquals(expectedResource.checksum(), actualMedia.checksum());
        assertEquals(expectedResource.name(), actualMedia.name());

        final var actualStored = storageService().storage().get(expectedLocation);

        assertEquals(expectedResource, actualStored);
    }

    @Test
    void Given_AValidVideoId_When_CallsGetResources_Should_ReturnIt() {
        final var expectedVideoId = VideoID.unique();
        final var expectedMediaType = VideoMediaType.VIDEO;
        final var expectedResource = resource(expectedMediaType);

        storageService.store("videoId-%s/type-%s".formatted(expectedVideoId.getValue(), expectedMediaType.name()), expectedResource);
        storageService.store("videoId-%s/type-%s".formatted(expectedVideoId.getValue(), VideoMediaType.BANNER.name()), resource(VideoMediaType.BANNER));
        storageService.store("videoId-%s/type-%s".formatted(expectedVideoId.getValue(), VideoMediaType.TRAILER.name()), resource(VideoMediaType.TRAILER));

        assertEquals(3, storageService().storage().size());

        final var actualResult = this.mediaResourceGateway.getResource(expectedVideoId, expectedMediaType);

        Assertions.assertThat(actualResult)
                .isPresent()
                .hasValueSatisfying(resource -> {
                    assertEquals(expectedResource.checksum(), resource.checksum());
                    assertEquals(expectedResource.contentType(), resource.contentType());
                    assertEquals(expectedResource.name(), resource.name());
                });
    }

    @Test
    void Given_AValidVideoIdAndInvalidType_When_CallsGetResources_Should_ReturnEmpty() {
        final var expectedVideoId = VideoID.unique();

        storageService.store("videoId-%s/type-%s".formatted(expectedVideoId.getValue(), VideoMediaType.VIDEO.name()), resource(VideoMediaType.VIDEO));
        storageService.store("videoId-%s/type-%s".formatted(expectedVideoId.getValue(), VideoMediaType.BANNER.name()), resource(VideoMediaType.BANNER));
        storageService.store("videoId-%s/type-%s".formatted(expectedVideoId.getValue(), VideoMediaType.TRAILER.name()), resource(VideoMediaType.TRAILER));

        assertEquals(3, storageService().storage().size());

        final var actualResult = this.mediaResourceGateway.getResource(expectedVideoId, VideoMediaType.THUMBNAIL);

        Assertions.assertThat(actualResult).isEmpty();
    }

    @Test
    void Given_AValidVideoId_When_CallsClearResources_Should_DeleteAll() {
        final var videoOne = VideoID.unique();
        final var videoTwo = VideoID.unique();

        final var toBeDeleted = List.of(
                "videoId-%s/type-%s".formatted(videoOne.getValue(), VideoMediaType.BANNER.name()),
                "videoId-%s/type-%s".formatted(videoOne.getValue(), VideoMediaType.TRAILER.name()),
                "videoId-%s/type-%s".formatted(videoOne.getValue(), VideoMediaType.VIDEO.name()));

        final var expectedValues = List.of(
                "videoId-%s/type-%s".formatted(videoTwo.getValue(), VideoMediaType.BANNER.name()),
                "videoId-%s/type-%s".formatted(videoTwo.getValue(), VideoMediaType.VIDEO.name()));

        toBeDeleted.forEach(id -> storageService.store(id, resource(videoMediaType())));
        expectedValues.forEach(id -> storageService.store(id, resource(videoMediaType())));

        assertEquals(5, storageService().storage().size());

        this.mediaResourceGateway.clearResources(videoOne);

        assertEquals(2, storageService().storage().size());

        assertThat(storageService().storage().keySet(), containsInAnyOrder(expectedValues.toArray()));
    }

    private InMemoryStorageService storageService() {
        return (InMemoryStorageService) this.storageService;
    }

}
