package com.github.allisson95.codeflix.application.video.media.upload;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.github.allisson95.codeflix.application.UseCaseTest;
import com.github.allisson95.codeflix.domain.Fixture;
import com.github.allisson95.codeflix.domain.exceptions.NotFoundException;
import com.github.allisson95.codeflix.domain.video.MediaResourceGateway;
import com.github.allisson95.codeflix.domain.video.VideoGateway;
import com.github.allisson95.codeflix.domain.video.VideoMediaType;
import com.github.allisson95.codeflix.domain.video.VideoResource;

class UploadMediaUseCaseTest extends UseCaseTest {

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @Mock
    private VideoGateway videoGateway;

    @InjectMocks
    private DefaultUploadMediaUseCase useCase;

    @Override
    protected List<Object> getMocksForClean() {
        return List.of(
                mediaResourceGateway,
                videoGateway);
    }

    @Test
    void Given_ACommandToUpload_When_IsValid_Should_UpdateBannerMediaAndPersistIt() {
        final var aVideo = Fixture.Videos.random();
        final var expectedId = aVideo.getId();
        final var expectedType = VideoMediaType.BANNER;
        final var expectedResource = Fixture.Videos.resource(expectedType);
        final var expectedVideoResource = VideoResource.with(expectedResource, expectedType);
        final var expectedMedia = Fixture.Videos.imageMedia(expectedType);

        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(aVideo));

        when(mediaResourceGateway.storeImage(any(), any()))
                .thenReturn(expectedMedia);

        when(videoGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var aCommand = UploadMediaCommand.with(expectedId.getValue(), expectedVideoResource);

        final var actualOutput = useCase.execute(aCommand);

        assertEquals(expectedId.getValue(), actualOutput.videoId());
        assertEquals(expectedType, actualOutput.mediaType());

        verify(videoGateway, times(1)).findById(expectedId);

        verify(mediaResourceGateway, times(1)).storeImage(expectedId, expectedVideoResource);

        verify(videoGateway, times(1))
                .update(argThat(actualVideo -> Objects.equals(expectedMedia, actualVideo.getBanner().get())
                        && actualVideo.getThumbnail().isEmpty()
                        && actualVideo.getThumbnailHalf().isEmpty()
                        && actualVideo.getTrailer().isEmpty()
                        && actualVideo.getVideo().isEmpty()));
    }

    @Test
    void Given_ACommandToUpload_When_IsValid_Should_UpdateThumbnailMediaAndPersistIt() {
        final var aVideo = Fixture.Videos.random();
        final var expectedId = aVideo.getId();
        final var expectedType = VideoMediaType.THUMBNAIL;
        final var expectedResource = Fixture.Videos.resource(expectedType);
        final var expectedVideoResource = VideoResource.with(expectedResource, expectedType);
        final var expectedMedia = Fixture.Videos.imageMedia(expectedType);

        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(aVideo));

        when(mediaResourceGateway.storeImage(any(), any()))
                .thenReturn(expectedMedia);

        when(videoGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var aCommand = UploadMediaCommand.with(expectedId.getValue(), expectedVideoResource);

        final var actualOutput = useCase.execute(aCommand);

        assertEquals(expectedId.getValue(), actualOutput.videoId());
        assertEquals(expectedType, actualOutput.mediaType());

        verify(videoGateway, times(1)).findById(expectedId);

        verify(mediaResourceGateway, times(1)).storeImage(expectedId, expectedVideoResource);

        verify(videoGateway, times(1))
                .update(argThat(actualVideo -> actualVideo.getBanner().isEmpty()
                        && Objects.equals(expectedMedia, actualVideo.getThumbnail().get())
                        && actualVideo.getThumbnailHalf().isEmpty()
                        && actualVideo.getTrailer().isEmpty()
                        && actualVideo.getVideo().isEmpty()));
    }

    @Test
    void Given_ACommandToUpload_When_IsValid_Should_UpdateThumbnailHalfMediaAndPersistIt() {
        final var aVideo = Fixture.Videos.random();
        final var expectedId = aVideo.getId();
        final var expectedType = VideoMediaType.THUMBNAIL_HALF;
        final var expectedResource = Fixture.Videos.resource(expectedType);
        final var expectedVideoResource = VideoResource.with(expectedResource, expectedType);
        final var expectedMedia = Fixture.Videos.imageMedia(expectedType);

        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(aVideo));

        when(mediaResourceGateway.storeImage(any(), any()))
                .thenReturn(expectedMedia);

        when(videoGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var aCommand = UploadMediaCommand.with(expectedId.getValue(), expectedVideoResource);

        final var actualOutput = useCase.execute(aCommand);

        assertEquals(expectedId.getValue(), actualOutput.videoId());
        assertEquals(expectedType, actualOutput.mediaType());

        verify(videoGateway, times(1)).findById(expectedId);

        verify(mediaResourceGateway, times(1)).storeImage(expectedId, expectedVideoResource);

        verify(videoGateway, times(1))
                .update(argThat(actualVideo -> actualVideo.getBanner().isEmpty()
                        && actualVideo.getThumbnail().isEmpty()
                        && Objects.equals(expectedMedia, actualVideo.getThumbnailHalf().get())
                        && actualVideo.getTrailer().isEmpty()
                        && actualVideo.getVideo().isEmpty()));
    }

    @Test
    void Given_ACommandToUpload_When_IsValid_Should_UpdateTrailerMediaAndPersistIt() {
        final var aVideo = Fixture.Videos.random();
        final var expectedId = aVideo.getId();
        final var expectedType = VideoMediaType.TRAILER;
        final var expectedResource = Fixture.Videos.resource(expectedType);
        final var expectedVideoResource = VideoResource.with(expectedResource, expectedType);
        final var expectedMedia = Fixture.Videos.videoMedia(expectedType);

        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(aVideo));

        when(mediaResourceGateway.storeVideo(any(), any()))
                .thenReturn(expectedMedia);

        when(videoGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var aCommand = UploadMediaCommand.with(expectedId.getValue(), expectedVideoResource);

        final var actualOutput = useCase.execute(aCommand);

        assertEquals(expectedId.getValue(), actualOutput.videoId());
        assertEquals(expectedType, actualOutput.mediaType());

        verify(videoGateway, times(1)).findById(expectedId);

        verify(mediaResourceGateway, times(1)).storeVideo(expectedId, expectedVideoResource);

        verify(videoGateway, times(1))
                .update(argThat(actualVideo -> actualVideo.getBanner().isEmpty()
                        && actualVideo.getThumbnail().isEmpty()
                        && actualVideo.getThumbnailHalf().isEmpty()
                        && Objects.equals(expectedMedia, actualVideo.getTrailer().get())
                        && actualVideo.getVideo().isEmpty()));
    }

    @Test
    void Given_ACommandToUpload_When_IsValid_Should_UpdateVideoMediaAndPersistIt() {
        final var aVideo = Fixture.Videos.random();
        final var expectedId = aVideo.getId();
        final var expectedType = VideoMediaType.VIDEO;
        final var expectedResource = Fixture.Videos.resource(expectedType);
        final var expectedVideoResource = VideoResource.with(expectedResource, expectedType);
        final var expectedMedia = Fixture.Videos.videoMedia(expectedType);

        when(videoGateway.findById(any()))
                .thenReturn(Optional.of(aVideo));

        when(mediaResourceGateway.storeVideo(any(), any()))
                .thenReturn(expectedMedia);

        when(videoGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var aCommand = UploadMediaCommand.with(expectedId.getValue(), expectedVideoResource);

        final var actualOutput = useCase.execute(aCommand);

        assertEquals(expectedId.getValue(), actualOutput.videoId());
        assertEquals(expectedType, actualOutput.mediaType());

        verify(videoGateway, times(1)).findById(expectedId);

        verify(mediaResourceGateway, times(1)).storeVideo(expectedId, expectedVideoResource);

        verify(videoGateway, times(1))
                .update(argThat(actualVideo -> actualVideo.getBanner().isEmpty()
                        && actualVideo.getThumbnail().isEmpty()
                        && actualVideo.getThumbnailHalf().isEmpty()
                        && actualVideo.getTrailer().isEmpty()
                        && Objects.equals(expectedMedia, actualVideo.getVideo().get())));
    }

    @Test
    void Given_ACommandToUpload_When_VideoIsInvalid_Should_ReturnNotFound() {
        final var aVideo = Fixture.Videos.random();
        final var expectedId = aVideo.getId();
        final var expectedType = VideoMediaType.VIDEO;
        final var expectedResource = Fixture.Videos.resource(expectedType);
        final var expectedVideoResource = VideoResource.with(expectedResource, expectedType);
        final var expectedErrorMessage = "Video with id %s was not found".formatted(expectedId.getValue());

        when(videoGateway.findById(any()))
                .thenReturn(Optional.empty());

        final var aCommand = UploadMediaCommand.with(expectedId.getValue(), expectedVideoResource);

        final var actualException = assertThrows(
                NotFoundException.class,
                () -> useCase.execute(aCommand));

        assertNotNull(actualException);
        assertEquals(expectedErrorMessage, actualException.getMessage());

        verify(videoGateway, times(1)).findById(expectedId);
        verify(mediaResourceGateway, times(0)).storeVideo(any(), any());
        verify(videoGateway, times(0)).update(any());
    }

}
