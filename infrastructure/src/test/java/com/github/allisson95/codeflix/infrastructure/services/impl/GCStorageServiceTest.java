package com.github.allisson95.codeflix.infrastructure.services.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.github.allisson95.codeflix.domain.Fixture;
import com.github.allisson95.codeflix.domain.resource.Resource;
import com.github.allisson95.codeflix.domain.utils.IdUtils;
import com.github.allisson95.codeflix.domain.video.VideoMediaType;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobListOption;

class GCStorageServiceTest {

    private GCStorageService target;

    private Storage storage;

    private String bucket = "fc3_test";

    @BeforeEach
    void setUp() {
        this.storage = Mockito.mock(Storage.class);
        this.target = new GCStorageService(this.bucket, this.storage);
    }

    @Test
    void Given_AValidResource_When_CallsStore_Should_StoreIt() {
        final var expectedName = IdUtils.uuid();
        final var expectedResource = Fixture.Videos.resource(VideoMediaType.VIDEO);

        final var blob = mockBlob(expectedName, expectedResource);
        doReturn(blob).when(storage).create(any(BlobInfo.class), any());

        this.target.store(expectedName, expectedResource);

        final var captor = ArgumentCaptor.forClass(BlobInfo.class);

        verify(storage, times(1)).create(captor.capture(), eq(expectedResource.content()));

        final var actualBlob = captor.getValue();

        assertEquals(this.bucket, actualBlob.getBlobId().getBucket());
        assertEquals(expectedName, actualBlob.getName());
        assertEquals(expectedName, actualBlob.getBlobId().getName());
        assertEquals(expectedResource.checksum(), actualBlob.getCrc32cToHexString());
        assertEquals(expectedResource.contentType(), actualBlob.getContentType());
    }

    @Test
    void Given_AValidResource_When_CallsGet_Should_RetrieveIt() {
        final var expectedName = IdUtils.uuid();
        final var expectedResource = Fixture.Videos.resource(VideoMediaType.VIDEO);

        final var blob = mockBlob(expectedName, expectedResource);
        doReturn(blob).when(storage).get(anyString(), anyString());

        final var actualResource = this.target.get(expectedName).get();

        verify(storage, times(1)).get(this.bucket, expectedName);

        assertEquals(expectedResource, actualResource);
    }

    @Test
    void Given_AInvalidResourceName_When_CallsGet_Should_BeEmpty() {
        final var expectedName = IdUtils.uuid();

        doReturn(null).when(storage).get(anyString(), anyString());

        final var actualResource = this.target.get(expectedName);

        verify(storage, times(1)).get(this.bucket, expectedName);

        assertTrue(actualResource.isEmpty());
    }

    @Test
    void Given_AValidPrefix_When_CallsList_Should_RetrieveAll() {
        final var expectedPrefix = "media_";

        final var expectedNameBanner = expectedPrefix + IdUtils.uuid();
        final var expectedBanner = Fixture.Videos.resource(VideoMediaType.BANNER);

        final var expectedNameVideo = expectedPrefix + IdUtils.uuid();
        final var expectedVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);

        final var expectedResources = List.of(expectedNameBanner, expectedNameVideo);

        final var blobBanner = mockBlob(expectedNameBanner, expectedBanner);
        final var blobVideo = mockBlob(expectedNameVideo, expectedVideo);

        final var page = mock(Page.class);
        doReturn(List.of(blobBanner, blobVideo)).when(page).iterateAll();
        doReturn(page).when(storage).list(anyString(), any());

        final var actualResources = this.target.list(expectedPrefix);

        verify(storage, times(1))
                .list(this.bucket, BlobListOption.prefix(expectedPrefix));

        assertThat(actualResources, containsInAnyOrder(expectedResources.toArray()));
    }

    @Test
    void Given_AValidNames_When_CallsDelete_Should_DeleteAll() {
        final var expectedPrefix = "media_";

        final var expectedNameBanner = expectedPrefix + IdUtils.uuid();
        final var expectedNameVideo = expectedPrefix + IdUtils.uuid();

        final var expectedResources = List.of(expectedNameBanner, expectedNameVideo);

        this.target.deleteAll(expectedResources);

        final var captor = ArgumentCaptor.forClass(List.class);

        verify(storage, times(1))
                .delete(captor.capture());

        final var actualResources = ((List<BlobId>) captor.getValue()).stream()
                .map(BlobId::getName)
                .toList();

        assertThat(actualResources, containsInAnyOrder(expectedResources.toArray()));
    }

    private Blob mockBlob(final String name, final Resource resource) {
        final var blob = Mockito.mock(Blob.class);
        when(blob.getBlobId()).thenReturn(BlobId.of(this.bucket, name));
        when(blob.getCrc32cToHexString()).thenReturn(resource.checksum());
        when(blob.getContent()).thenReturn(resource.content());
        when(blob.getContentType()).thenReturn(resource.contentType());
        when(blob.getName()).thenReturn(resource.name());
        return blob;
    }

}
