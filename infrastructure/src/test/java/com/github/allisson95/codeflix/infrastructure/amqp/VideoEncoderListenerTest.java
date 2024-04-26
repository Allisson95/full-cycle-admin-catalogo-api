package com.github.allisson95.codeflix.infrastructure.amqp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.amqp.rabbit.test.TestRabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.github.allisson95.codeflix.AmqpTest;
import com.github.allisson95.codeflix.application.video.media.update.UpdateMediaStatusCommand;
import com.github.allisson95.codeflix.application.video.media.update.UpdateMediaStatusUseCase;
import com.github.allisson95.codeflix.domain.utils.IdUtils;
import com.github.allisson95.codeflix.domain.video.MediaStatus;
import com.github.allisson95.codeflix.infrastructure.configuration.annotations.VideoEncodedQueue;
import com.github.allisson95.codeflix.infrastructure.configuration.json.Json;
import com.github.allisson95.codeflix.infrastructure.configuration.properties.amqp.QueueProperties;
import com.github.allisson95.codeflix.infrastructure.video.models.VideoEncoderCompleted;
import com.github.allisson95.codeflix.infrastructure.video.models.VideoEncoderError;
import com.github.allisson95.codeflix.infrastructure.video.models.VideoMessage;
import com.github.allisson95.codeflix.infrastructure.video.models.VideoMetadata;

@AmqpTest
class VideoEncoderListenerTest {

    @Autowired
    private TestRabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitListenerTestHarness harness;

    @Autowired
    @VideoEncodedQueue
    private QueueProperties queueProperties;

    @MockBean
    private UpdateMediaStatusUseCase useCase;

    @Test
    void Given_ErrorResult_When_CallsListener_Then_DoNothing() throws Exception {
        // given
        final var expectedError = new VideoEncoderError(
                new VideoMessage("123", "abc"),
                "Video not found");

        final var expectedMessage = Json.writeValueAsString(expectedError);

        // when
        doNothing().when(useCase).execute(any());

        this.rabbitTemplate.convertAndSend(queueProperties.getQueue(), expectedMessage);

        // then
        final var invocationData = harness.getNextInvocationDataFor(
                VideoEncoderListener.LISTENER_ID,
                1,
                TimeUnit.SECONDS);

        assertNotNull(invocationData);
        assertNotNull(invocationData.getArguments());

        final var actualMessage = (String) invocationData.getArguments()[0];
        assertEquals(expectedMessage, actualMessage);

        verify(useCase, times(0)).execute(any());
    }

    @Test
    void Given_CompletedResult_When_CallsListener_Then_CallUseCase() throws Exception {
        // given
        final var expectedId = IdUtils.uuid();
        final var expectedOutputBucket = "codeeducationtest";
        final var expectedStatus = MediaStatus.COMPLETED;
        final var expectedEncoderVideoFolder = "anyfolder";
        final var expectedResourceId = IdUtils.uuid();
        final var expectedFilePath = "any.mp4";
        final var expectedMetadata = new VideoMetadata(
                expectedEncoderVideoFolder,
                expectedResourceId,
                expectedFilePath);

        final var expectedCompleted = new VideoEncoderCompleted(expectedId, expectedOutputBucket, expectedMetadata);

        final var expectedMessage = Json.writeValueAsString(expectedCompleted);

        // when
        doNothing().when(useCase).execute(any());

        this.rabbitTemplate.convertAndSend(queueProperties.getQueue(), expectedMessage);

        // then
        final var invocationData = harness.getNextInvocationDataFor(
                VideoEncoderListener.LISTENER_ID,
                1,
                TimeUnit.SECONDS);

        assertNotNull(invocationData);
        assertNotNull(invocationData.getArguments());

        final var actualMessage = (String) invocationData.getArguments()[0];
        assertEquals(expectedMessage, actualMessage);

        final var captor = ArgumentCaptor.forClass(UpdateMediaStatusCommand.class);
        verify(useCase, times(1)).execute(captor.capture());

        final var aCommand = captor.getValue();

        assertNotNull(aCommand);
        assertEquals(expectedStatus, aCommand.status());
        assertEquals(expectedId, aCommand.videoId());
        assertEquals(expectedResourceId, aCommand.resourceId());
        assertEquals(expectedEncoderVideoFolder, aCommand.folder());
        assertEquals(expectedFilePath, aCommand.filename());
    }

}
