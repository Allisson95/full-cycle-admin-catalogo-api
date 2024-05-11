package com.github.allisson95.codeflix.domain.video;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.github.allisson95.codeflix.domain.UnitTest;
import com.github.allisson95.codeflix.domain.utils.IdUtils;

class VideoMediaTest extends UnitTest {

    @Test
    void Given_AValidParams_When_CallsWith_Should_InstantiateIt() {
        final var expectedId = IdUtils.uuid();
        final var expectedChecksum = "d41d8cd98f00b204e9800998ecf8427e";
        final var expectedName = "Teste";
        final var expectedRawLocation = "/medias/raw";
        final var expectedEncodedLocation = "/medias/encoded";
        final var expectedStatus = MediaStatus.PROCESSING;

        final var aMedia = VideoMedia.with(
                expectedId,
                expectedChecksum,
                expectedName,
                expectedRawLocation,
                expectedEncodedLocation,
                expectedStatus);

        assertNotNull(aMedia);
        assertEquals(expectedId, aMedia.id());
        assertEquals(expectedChecksum, aMedia.checksum());
        assertEquals(expectedName, aMedia.name());
        assertEquals(expectedRawLocation, aMedia.rawLocation());
        assertEquals(expectedEncodedLocation, aMedia.encodedLocation());
        assertEquals(expectedStatus, aMedia.status());
    }

    @Test
    void Given_TwoMediasWithSameChecksumAndRawLocation_When_CallsEquals_Should_ReturnTrue() {
        final var aMediaOne = VideoMedia.with(
                IdUtils.uuid(),
                "d41d8cd98f00b204e9800998ecf8427e",
                "Teste One",
                "/medias/raw",
                "/medias/encoded",
                MediaStatus.PROCESSING);

        final var aMediaTwo = VideoMedia.with(
                IdUtils.uuid(),
                "d41d8cd98f00b204e9800998ecf8427e",
                "Teste Two",
                "/medias/raw",
                "/medias/encoded",
                MediaStatus.COMPLETED);

        assertEquals(aMediaOne, aMediaTwo);
        assertNotSame(aMediaOne, aMediaTwo);
    }

    @Test
    void Given_InvalidParams_When_CallsWith_Should_ReturnError() {
        assertThrows(
                NullPointerException.class,
                () -> VideoMedia.with(
                        null,
                        "d41d8cd98f00b204e9800998ecf8427e",
                        "Teste Two",
                        "/medias/raw",
                        "/medias/encoded",
                        MediaStatus.COMPLETED));

        assertThrows(
                NullPointerException.class,
                () -> VideoMedia.with(
                        IdUtils.uuid(),
                        null,
                        "Teste Two",
                        "/medias/raw",
                        "/medias/encoded",
                        MediaStatus.COMPLETED));

        assertThrows(
                NullPointerException.class,
                () -> VideoMedia.with(
                        IdUtils.uuid(),
                        "d41d8cd98f00b204e9800998ecf8427e",
                        null,
                        "/medias/raw",
                        "/medias/encoded",
                        MediaStatus.COMPLETED));

        assertThrows(
                NullPointerException.class,
                () -> VideoMedia.with(
                        IdUtils.uuid(),
                        "d41d8cd98f00b204e9800998ecf8427e",
                        "Teste Two",
                        null,
                        "/medias/encoded",
                        MediaStatus.COMPLETED));

        assertThrows(
                NullPointerException.class,
                () -> VideoMedia.with(
                        IdUtils.uuid(),
                        "d41d8cd98f00b204e9800998ecf8427e",
                        "Teste Two",
                        "/medias/raw",
                        null,
                        MediaStatus.COMPLETED));

        assertThrows(
                NullPointerException.class,
                () -> VideoMedia.with(
                        IdUtils.uuid(),
                        "d41d8cd98f00b204e9800998ecf8427e",
                        "Teste Two",
                        "/medias/raw",
                        "/medias/encoded",
                        null));
    }

}
