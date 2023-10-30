package com.github.allisson95.codeflix.domain.video;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ImageMediaTest {

    @Test
    void Given_AValidParams_When_CallsWith_Should_InstantiateIt() {
        final var expectedChecksum = "d41d8cd98f00b204e9800998ecf8427e";
        final var expectedName = "Teste";
        final var expectedLocation = "/medias";

        final var aMedia = ImageMedia.with(
                expectedChecksum,
                expectedName,
                expectedLocation);

        assertNotNull(aMedia);
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
                () -> ImageMedia.with(null, "Teste Two", "/medias"));

        assertThrows(
                NullPointerException.class,
                () -> ImageMedia.with("d41d8cd98f00b204e9800998ecf8427e", null, "/medias"));

        assertThrows(
                NullPointerException.class,
                () -> ImageMedia.with("d41d8cd98f00b204e9800998ecf8427e", "Teste Two", null));

    }

}
