package com.github.allisson95.codeflix.application.video.media.get;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.github.allisson95.codeflix.application.UseCaseTest;
import com.github.allisson95.codeflix.domain.Fixture;
import com.github.allisson95.codeflix.domain.exceptions.NotFoundException;
import com.github.allisson95.codeflix.domain.video.MediaResourceGateway;
import com.github.allisson95.codeflix.domain.video.VideoID;

class GetMediaUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultGetMediaUseCase useCase;

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @Override
    protected List<Object> getMocksForClean() {
        return List.of(mediaResourceGateway);
    }

    @Test
    void Given_VideoIdAndType_When_IsValidCommand_Should_ReturnResource() {
        final var expectedId = VideoID.unique();
        final var expectedType = Fixture.Videos.videoMediaType();
        final var expectedResource = Fixture.Videos.resource(expectedType);

        when(mediaResourceGateway.getResource(expectedId, expectedType))
                .thenReturn(Optional.of(expectedResource));

        final var aCommand = GetMediaCommand.with(expectedId.getValue(), expectedType.name());

        final var actualResult = this.useCase.execute(aCommand);

        assertEquals(expectedResource.name(), actualResult.name());
        assertEquals(expectedResource.content(), actualResult.content());
        assertEquals(expectedResource.contentType(), actualResult.contentType());
    }

    @Test
    void Given_VideoIdAndType_When_MediaTypeDoesntExists_Should_ReturnNotFoundException() {
        final var expectedId = VideoID.unique();
        final var expectedType = "NÃO_EXISTE";
        final var expectedErrorMessage = "Media type %s doesn't exists".formatted(expectedType);

        final var aCommand = GetMediaCommand.with(expectedId.getValue(), expectedType);

        final var actualException = assertThrows(
                NotFoundException.class,
                () -> this.useCase.execute(aCommand));

        assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    @Test
    void Given_VideoIdAndType_When_IsNotFound_Should_ReturnNotFoundException() {
        final var expectedId = VideoID.unique();
        final var expectedType = Fixture.Videos.videoMediaType();
        final var expectedErrorMessage = "Resource %s not found for video %s".formatted(expectedType.name(), expectedId.getValue());

        when(mediaResourceGateway.getResource(expectedId, expectedType))
                .thenReturn(Optional.empty());

        final var aCommand = GetMediaCommand.with(expectedId.getValue(), expectedType.name());

        final var actualException = assertThrows(
                NotFoundException.class,
                () -> this.useCase.execute(aCommand));

        assertEquals(expectedErrorMessage, actualException.getMessage());
    }

}
