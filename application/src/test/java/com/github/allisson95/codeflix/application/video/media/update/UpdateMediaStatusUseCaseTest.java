package com.github.allisson95.codeflix.application.video.media.update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.github.allisson95.codeflix.application.UseCaseTest;
import com.github.allisson95.codeflix.domain.Fixture;
import com.github.allisson95.codeflix.domain.exceptions.NotFoundException;
import com.github.allisson95.codeflix.domain.video.MediaStatus;
import com.github.allisson95.codeflix.domain.video.Video;
import com.github.allisson95.codeflix.domain.video.VideoGateway;
import com.github.allisson95.codeflix.domain.video.VideoID;
import com.github.allisson95.codeflix.domain.video.VideoMediaType;

class UpdateMediaStatusUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultUpdateMediaStatusUseCase useCase;

    @Mock
    private VideoGateway videoGateway;

    @Override
    protected List<Object> getMocksForClean() {
        return List.of(videoGateway);
    }

    @Test
    void Given_ACommandForProcessingTrailerMedia_When_IsValid_Should_UpdateStatusAndEncodedLocation() {
        final var expectedStatus = MediaStatus.PROCESSING;
        final String expectedFolder = null;
        final String expectedFilename = null;
        final var expectedType = VideoMediaType.TRAILER;
        final var expectedMedia = Fixture.Videos.videoMedia(expectedType);

        final var aVideo = Fixture.Videos.random()
                .updateTrailerMedia(expectedMedia);

        final var expectedId = aVideo.getId();

        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(aVideo));

        when(videoGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var aCommand = UpdateMediaStatusCommand.with(
                expectedStatus,
                expectedId.getValue(),
                expectedMedia.id(),
                expectedFolder,
                expectedFilename);

        this.useCase.execute(aCommand);

        verify(videoGateway, times(1)).findById(expectedId);

        final var captor = ArgumentCaptor.forClass(Video.class);

        verify(videoGateway, times(1)).update(captor.capture());

        final var actualVideo = captor.getValue();

        assertTrue(actualVideo.getVideo().isEmpty());

        final var actualVideoMedia = actualVideo.getTrailer().get();

        assertEquals(expectedMedia.id(), actualVideoMedia.id());
        assertEquals(expectedMedia.rawLocation(), actualVideoMedia.rawLocation());
        assertEquals(expectedMedia.checksum(), actualVideoMedia.checksum());
        assertEquals(expectedStatus, actualVideoMedia.status());
        assertTrue(actualVideoMedia.encodedLocation().isBlank());
    }

    @Test
    void Given_ACommandForProcessingVideoMedia_When_IsValid_Should_UpdateStatusAndEncodedLocation() {
        final var expectedStatus = MediaStatus.PROCESSING;
        final String expectedFolder = null;
        final String expectedFilename = null;
        final var expectedType = VideoMediaType.VIDEO;
        final var expectedMedia = Fixture.Videos.videoMedia(expectedType);

        final var aVideo = Fixture.Videos.random()
                .updateVideoMedia(expectedMedia);

        final var expectedId = aVideo.getId();

        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(aVideo));

        when(videoGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var aCommand = UpdateMediaStatusCommand.with(
                expectedStatus,
                expectedId.getValue(),
                expectedMedia.id(),
                expectedFolder,
                expectedFilename);

        this.useCase.execute(aCommand);

        verify(videoGateway, times(1)).findById(expectedId);

        final var captor = ArgumentCaptor.forClass(Video.class);

        verify(videoGateway, times(1)).update(captor.capture());

        final var actualVideo = captor.getValue();

        assertTrue(actualVideo.getTrailer().isEmpty());

        final var actualVideoMedia = actualVideo.getVideo().get();

        assertEquals(expectedMedia.id(), actualVideoMedia.id());
        assertEquals(expectedMedia.rawLocation(), actualVideoMedia.rawLocation());
        assertEquals(expectedMedia.checksum(), actualVideoMedia.checksum());
        assertEquals(expectedStatus, actualVideoMedia.status());
        assertTrue(actualVideoMedia.encodedLocation().isBlank());
    }

    @Test
    void Given_ACommandForCompleteTrailerMedia_When_IsValid_Should_UpdateStatusAndEncodedLocation() {
        final var expectedStatus = MediaStatus.COMPLETED;
        final var expectedFolder = "encoded_media";
        final var expectedFilename = "filename.mp4";
        final var expectedType = VideoMediaType.TRAILER;
        final var expectedMedia = Fixture.Videos.videoMedia(expectedType);

        final var aVideo = Fixture.Videos.random()
                .updateTrailerMedia(expectedMedia);

        final var expectedId = aVideo.getId();

        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(aVideo));

        when(videoGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var aCommand = UpdateMediaStatusCommand.with(
                expectedStatus,
                expectedId.getValue(),
                expectedMedia.id(),
                expectedFolder,
                expectedFilename);

        this.useCase.execute(aCommand);

        verify(videoGateway, times(1)).findById(expectedId);

        final var captor = ArgumentCaptor.forClass(Video.class);

        verify(videoGateway, times(1)).update(captor.capture());

        final var actualVideo = captor.getValue();

        assertTrue(actualVideo.getVideo().isEmpty());

        final var actualVideoMedia = actualVideo.getTrailer().get();

        assertEquals(expectedMedia.id(), actualVideoMedia.id());
        assertEquals(expectedMedia.rawLocation(), actualVideoMedia.rawLocation());
        assertEquals(expectedMedia.checksum(), actualVideoMedia.checksum());
        assertEquals(expectedStatus, actualVideoMedia.status());
        assertEquals(expectedFolder.concat("/").concat(expectedFilename), actualVideoMedia.encodedLocation());
    }

    @Test
    void Given_ACommandForCompleteVideoMedia_When_IsValid_Should_UpdateStatusAndEncodedLocation() {
        final var expectedStatus = MediaStatus.COMPLETED;
        final var expectedFolder = "encoded_media";
        final var expectedFilename = "filename.mp4";
        final var expectedType = VideoMediaType.VIDEO;
        final var expectedMedia = Fixture.Videos.videoMedia(expectedType);

        final var aVideo = Fixture.Videos.random()
                .updateVideoMedia(expectedMedia);

        final var expectedId = aVideo.getId();

        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(aVideo));

        when(videoGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var aCommand = UpdateMediaStatusCommand.with(
                expectedStatus,
                expectedId.getValue(),
                expectedMedia.id(),
                expectedFolder,
                expectedFilename);

        this.useCase.execute(aCommand);

        verify(videoGateway, times(1)).findById(expectedId);

        final var captor = ArgumentCaptor.forClass(Video.class);

        verify(videoGateway, times(1)).update(captor.capture());

        final var actualVideo = captor.getValue();

        assertTrue(actualVideo.getTrailer().isEmpty());

        final var actualVideoMedia = actualVideo.getVideo().get();

        assertEquals(expectedMedia.id(), actualVideoMedia.id());
        assertEquals(expectedMedia.rawLocation(), actualVideoMedia.rawLocation());
        assertEquals(expectedMedia.checksum(), actualVideoMedia.checksum());
        assertEquals(expectedStatus, actualVideoMedia.status());
        assertEquals(expectedFolder.concat("/").concat(expectedFilename), actualVideoMedia.encodedLocation());
    }

    @Test
    void Given_ACommandForCompleteVideoMedia_When_IsInvalidMediaId_Should_DoNothing() {
        final var expectedStatus = MediaStatus.COMPLETED;
        final var expectedFolder = "encoded_media";
        final var expectedFilename = "filename.mp4";
        final var expectedType = VideoMediaType.VIDEO;
        final var expectedMedia = Fixture.Videos.videoMedia(expectedType);

        final var aVideo = Fixture.Videos.random()
                .updateVideoMedia(expectedMedia);

        final var expectedId = aVideo.getId();

        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(aVideo));

        final var aCommand = UpdateMediaStatusCommand.with(
                expectedStatus,
                expectedId.getValue(),
                "randomId",
                expectedFolder,
                expectedFilename);

        this.useCase.execute(aCommand);

        verify(videoGateway, times(1)).findById(expectedId);
        verify(videoGateway, times(0)).update(any());
    }

    @Test
    void Given_ACommandForCompleteVideoMedia_When_IsInvalidVideoId_Should_ReturnNotFound() {
        final var expectedId = VideoID.unique();
        final var expectedErrorMessage = "Video with id %s was not found".formatted(expectedId.getValue());

        when(videoGateway.findById(any()))
                .thenReturn(Optional.empty());

        final var aCommand = UpdateMediaStatusCommand.with(
                MediaStatus.COMPLETED,
                expectedId.getValue(),
                Fixture.Videos.videoMedia(VideoMediaType.VIDEO).id(),
                "encoded_media",
                "filename.mp4");

        final var actualException = assertThrows(
                NotFoundException.class,
                () -> this.useCase.execute(aCommand));

        assertNotNull(actualException);
        assertEquals(expectedErrorMessage, actualException.getMessage());

        verify(videoGateway, times(1)).findById(expectedId);
        verify(videoGateway, times(0)).update(any());
    }

}
