package com.github.allisson95.codeflix.infrastructure.configuration;

import org.springframework.amqp.rabbit.core.RabbitOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.allisson95.codeflix.infrastructure.configuration.annotations.VideoCreatedQueue;
import com.github.allisson95.codeflix.infrastructure.configuration.properties.amqp.QueueProperties;
import com.github.allisson95.codeflix.infrastructure.services.EventService;
import com.github.allisson95.codeflix.infrastructure.services.impl.RabbitEventService;

@Configuration
class EventConfig {

    @Bean
    @VideoCreatedQueue
    EventService videoCreated(
            @VideoCreatedQueue final QueueProperties properties,
            final RabbitOperations operations) {
        return new RabbitEventService(properties.getExchange(), properties.getRoutingKey(), operations);
    }

}
