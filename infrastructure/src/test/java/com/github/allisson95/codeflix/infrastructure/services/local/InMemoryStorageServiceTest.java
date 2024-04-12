package com.github.allisson95.codeflix.infrastructure.services.local;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.allisson95.codeflix.domain.Fixture;
import com.github.allisson95.codeflix.domain.utils.IdUtils;
import com.github.allisson95.codeflix.domain.video.VideoMediaType;
import com.github.allisson95.codeflix.infrastructure.services.StorageService;

class InMemoryStorageServiceTest {

    private StorageService target = new InMemoryStorageService();

    @BeforeEach
    void setUp() {
        ((InMemoryStorageService) this.target).reset();
    }

    @Test
    void Given_AValidResource_When_CallsStore_Should_StoreIt() {
        final var expectedName = IdUtils.uuid();
        final var expectedResource = Fixture.Videos.resource(VideoMediaType.VIDEO);

        target.store(expectedName, expectedResource);

        assertEquals(expectedResource, target.get(expectedName).get());
    }

    @Test
    void Given_AValidResource_When_CallsGet_Should_RetrieveIt() {
        final var expectedName = IdUtils.uuid();
        final var expectedResource = Fixture.Videos.resource(VideoMediaType.VIDEO);

        ((InMemoryStorageService) this.target).storage().put(expectedName, expectedResource);

        final var actualResource = target.get(expectedName).get();

        assertEquals(expectedResource, actualResource);
    }

    @Test
    void Given_AInvalidResourceName_When_CallsGet_Should_BeEmpty() {
        final var expectedName = IdUtils.uuid();

        final var actualResource = target.get(expectedName);

        assertTrue(actualResource.isEmpty());
    }

    @Test
    void Given_AValidPrefix_When_CallsList_Should_RetrieveAll() {
        final var expectedNames = List.of(
                "video_" + IdUtils.uuid(),
                "video_" + IdUtils.uuid(),
                "video_" + IdUtils.uuid());

        final var all = new ArrayList<>(expectedNames);
        all.add("image_" + IdUtils.uuid());
        all.add("image_" + IdUtils.uuid());

        all.forEach(name -> ((InMemoryStorageService) this.target).storage()
                .put(name, Fixture.Videos.resource(VideoMediaType.VIDEO)));

        assertEquals(5, ((InMemoryStorageService) this.target).storage().size());

        final var actualResource = target.list("video");

        assertThat(actualResource, containsInAnyOrder(expectedNames.toArray()));
    }

    @Test
    void Given_AValidNames_When_CallsDelete_Should_DeleteAll() {
        final var expectedNames = List.of(
                "video_" + IdUtils.uuid(),
                "video_" + IdUtils.uuid(),
                "video_" + IdUtils.uuid());

        final var images = List.of(
                "image_" + IdUtils.uuid(),
                "image_" + IdUtils.uuid());

        final var all = new ArrayList<>(expectedNames);
        all.addAll(images);

        all.forEach(name -> ((InMemoryStorageService) this.target).storage()
                .put(name, Fixture.Videos.resource(VideoMediaType.VIDEO)));

        assertEquals(5, ((InMemoryStorageService) this.target).storage().size());

        target.deleteAll(images);

        assertEquals(3, ((InMemoryStorageService) this.target).storage().size());
        assertThat(((InMemoryStorageService) this.target).storage().keySet(),
                containsInAnyOrder(expectedNames.toArray()));
    }

}
