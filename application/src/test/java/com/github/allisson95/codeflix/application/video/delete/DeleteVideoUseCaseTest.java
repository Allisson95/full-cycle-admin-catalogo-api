package com.github.allisson95.codeflix.application.video.delete;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.github.allisson95.codeflix.application.UseCaseTest;
import com.github.allisson95.codeflix.domain.exceptions.InternalErrorException;
import com.github.allisson95.codeflix.domain.video.MediaResourceGateway;
import com.github.allisson95.codeflix.domain.video.VideoGateway;
import com.github.allisson95.codeflix.domain.video.VideoID;

class DeleteVideoUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultDeleteVideoUseCase useCase;

    @Mock
    private VideoGateway videoGateway;

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @Override
    protected List<Object> getMocksForClean() {
        return List.of(
                videoGateway,
                mediaResourceGateway);
    }

    @Test
    void Given_AValidId_When_CallsDeleteVideo_Should_DeleteIt() {
        final var videoId = VideoID.unique();

        doNothing()
                .when(videoGateway).deleteById(any());

        doNothing()
                .when(mediaResourceGateway).clearResources(any());

        assertDoesNotThrow(() -> this.useCase.execute(videoId.getValue()));

        verify(videoGateway).deleteById(videoId);
        verify(mediaResourceGateway).clearResources(videoId);
    }

    @Test
    void Given_AInvalidId_When_CallsDeleteVideo_Should_DoNothing() {
        final var videoId = VideoID.from("invalid");

        doNothing()
                .when(videoGateway).deleteById(any());

        doNothing()
                .when(mediaResourceGateway).clearResources(any());

        assertDoesNotThrow(() -> this.useCase.execute(videoId.getValue()));

        verify(videoGateway).deleteById(videoId);
        verify(mediaResourceGateway).clearResources(videoId);
    }

    @Test
    void Given_AValidId_When_CallsDeleteVideoAndThrowsAnException_Should_ReceiveAnException() {
        final var videoId = VideoID.unique();

        doThrow(new InternalErrorException("Error on delete video", new RuntimeException()))
                .when(videoGateway).deleteById(any());

        assertThrows(InternalErrorException.class, () -> this.useCase.execute(videoId.getValue()));

        verify(videoGateway).deleteById(videoId);
    }

}
