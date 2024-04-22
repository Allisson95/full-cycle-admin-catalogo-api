package com.github.allisson95.codeflix.infrastructure.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.allisson95.codeflix.infrastructure.configuration.annotations.VideoCreatedQueue;
import com.github.allisson95.codeflix.infrastructure.configuration.annotations.VideoEncodedQueue;
import com.github.allisson95.codeflix.infrastructure.configuration.annotations.VideoEvents;
import com.github.allisson95.codeflix.infrastructure.configuration.properties.amqp.QueueProperties;

@Configuration
public class AmqpConfig {

    @Bean
    @ConfigurationProperties(prefix = "amqp.queues.video-created")
    @VideoCreatedQueue
    QueueProperties videoCreatedQueueProperties() {
        return new QueueProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "amqp.queues.video-encoded")
    @VideoEncodedQueue
    QueueProperties videoEncodedQueueProperties() {
        return new QueueProperties();
    }

    @Configuration
    static class Admin {

        @Bean
        @VideoEvents
        Exchange videoEventsExchange(@VideoCreatedQueue final QueueProperties properties) {
            return new DirectExchange(properties.getExchange());
        }

        @Bean
        @VideoCreatedQueue
        Queue videoCreatedQueue(@VideoCreatedQueue final QueueProperties properties) {
            return new Queue(properties.getQueue());
        }

        @Bean
        @VideoCreatedQueue
        Binding videoCreatedBinding(
                @VideoEvents Exchange exchange,
                @VideoCreatedQueue Queue queue,
                @VideoCreatedQueue QueueProperties properties) {
            return BindingBuilder
                    .bind(queue)
                    .to((DirectExchange) exchange)
                    .with(properties.getRoutingKey());
        }

        @Bean
        @VideoEncodedQueue
        Queue videoEncodedQueue(@VideoEncodedQueue final QueueProperties properties) {
            return new Queue(properties.getQueue());
        }

        @Bean
        @VideoEncodedQueue
        Binding videoEncodedBinding(
                @VideoEvents Exchange exchange,
                @VideoEncodedQueue Queue queue,
                @VideoEncodedQueue QueueProperties properties) {
            return BindingBuilder
                    .bind(queue)
                    .to((DirectExchange) exchange)
                    .with(properties.getRoutingKey());
        }

    }

}
