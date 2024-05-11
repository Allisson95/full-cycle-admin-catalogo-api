package com.github.allisson95.codeflix.domain.video;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.github.allisson95.codeflix.domain.UnitTest;
import com.github.allisson95.codeflix.domain.utils.IdUtils;

class ImageMediaTest extends UnitTest {

    @Test
    void Given_AValidParams_When_CallsWith_Should_InstantiateIt() {
        final var expectedId = IdUtils.uuid();
        final var expectedChecksum = "d41d8cd98f00b204e9800998ecf8427e";
        final var expectedName = "Teste";
        final var expectedLocation = "/medias";

        final var aMedia = ImageMedia.with(
                expectedId,
                expectedChecksum,
                expectedName,
                expectedLocation);

        assertNotNull(aMedia);
        assertEquals(expectedId, aMedia.id());
        assertEquals(expectedChecksum, aMedia.checksum());
        assertEquals(expectedName, aMedia.name());
        assertEquals(expectedLocation, aMedia.location());
    }

    @Test
    void Given_TwoMediasWithSameChecksumAndRawLocation_When_CallsEquals_Should_ReturnTrue() {
        final var aMediaOne = ImageMedia.with("d41d8cd98f00b204e9800998ecf8427e", "Teste One", "/medias");
        final var aMediaTwo = ImageMedia.with("d41d8cd98f00b204e9800998ecf8427e", "Teste Two", "/medias");

        assertEquals(aMediaOne, aMediaTwo);
        assertNotSame(aMediaOne, aMediaTwo);
    }

    @Test
    void Given_InvalidParams_When_CallsWith_Should_ReturnError() {
        assertThrows(
                NullPointerException.class,
                () -> ImageMedia.with(null, "d41d8cd98f00b204e9800998ecf8427e", "Teste Two", "/medias"));

        assertThrows(
                NullPointerException.class,
                () -> ImageMedia.with(IdUtils.uuid(), null, "Teste Two", "/medias"));

        assertThrows(
                NullPointerException.class,
                () -> ImageMedia.with(IdUtils.uuid(), "d41d8cd98f00b204e9800998ecf8427e", null, "/medias"));

        assertThrows(
                NullPointerException.class,
                () -> ImageMedia.with(IdUtils.uuid(), "d41d8cd98f00b204e9800998ecf8427e", "Teste Two", null));

    }

}
