package com.github.allisson95.codeflix.infrastructure.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.github.allisson95.codeflix.AmqpTest;
import com.github.allisson95.codeflix.domain.video.VideoMediaCreated;
import com.github.allisson95.codeflix.infrastructure.configuration.annotations.VideoCreatedQueue;
import com.github.allisson95.codeflix.infrastructure.configuration.json.Json;
import com.github.allisson95.codeflix.infrastructure.services.EventService;

@AmqpTest
class RabbitEventServiceTest {

    private static final String LISTENER = "video.created";

    @Autowired
    @VideoCreatedQueue
    private EventService eventService;

    @Autowired
    private RabbitListenerTestHarness harness;

    @Test
    void Should_SendMessage() throws Exception {
        final var notification = new VideoMediaCreated("resource", "filepath");

        final var expectedMessage = Json.writeValueAsString(notification);

        this.eventService.send(notification);

        final var invocationData = harness.getNextInvocationDataFor(LISTENER, 1, TimeUnit.SECONDS);

        assertNotNull(invocationData);
        assertNotNull(invocationData.getArguments());

        final var actualMessage = (String) invocationData.getArguments()[0];

        assertEquals(expectedMessage, actualMessage);
    }

    @Component
    static class VideoCreatedNewsListener {

        @RabbitListener(id = LISTENER, queues = "${amqp.queues.video-created.routing-key}")
        void onVideoCreated(@Payload String message) {

        }

    }

}
