package com.github.allisson95.codeflix.infrastructure.amqp;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.github.allisson95.codeflix.application.video.media.update.UpdateMediaStatusCommand;
import com.github.allisson95.codeflix.application.video.media.update.UpdateMediaStatusUseCase;
import com.github.allisson95.codeflix.domain.video.MediaStatus;
import com.github.allisson95.codeflix.infrastructure.configuration.json.Json;
import com.github.allisson95.codeflix.infrastructure.video.models.VideoEncoderCompleted;
import com.github.allisson95.codeflix.infrastructure.video.models.VideoEncoderError;
import com.github.allisson95.codeflix.infrastructure.video.models.VideoEncoderResult;

@Component
public class VideoEncoderListener {

    private static final Logger logger = LoggerFactory.getLogger(VideoEncoderListener.class);

    private static final String LISTENER_ID = "videoEncodedListener";

    private final UpdateMediaStatusUseCase useCase;

    public VideoEncoderListener(final UpdateMediaStatusUseCase useCase) {
        this.useCase = Objects.requireNonNull(useCase);
    }

    @RabbitListener(id = LISTENER_ID, queues = { "${amqp.queues.video-encoded.queue}" })
    public void onVideoEncodedMessage(@Payload final String message) {
        final var aResult = Json.readValue(message, VideoEncoderResult.class);

        if (aResult instanceof VideoEncoderCompleted completed) {
            logger.debug("[message:video.listener.income] [status:completed] [payload:{}]", message);
            final var aCommand = UpdateMediaStatusCommand.with(
                    MediaStatus.COMPLETED,
                    completed.id(),
                    completed.video().resourceId(),
                    completed.video().encodedVideoFolder(),
                    completed.video().filePath());

            this.useCase.execute(aCommand);
        } else if (aResult instanceof VideoEncoderError) {
            logger.error("[message:video.listener.income] [status:error] [payload:{}]", message);
        } else {
            logger.error("[message:video.listener.income] [status:unknown] [payload:{}]", message);
        }

    }

}
