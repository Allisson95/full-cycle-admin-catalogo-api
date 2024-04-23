package com.github.allisson95.codeflix;

import static org.mockito.Mockito.mock;

import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.amqp.rabbit.test.TestRabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rabbitmq.client.Channel;

/**
 * Creates proxy around each class annotated with {@link RabbitListener}
 * that can be used to verify incoming messages via
 * {@link RabbitListenerTestHarness}
 */
@Configuration
@RabbitListenerTest(spy = false, capture = true)
class AmqpTestConfiguration {

    @Bean
    ConnectionFactory connectionFactory() {
        final var factory = mock(ConnectionFactory.class);
        final var connection = mock(Connection.class);
        final var channel = mock(Channel.class);

        BDDMockito.willReturn(connection).given(factory).createConnection();
        BDDMockito.willReturn(channel).given(connection).createChannel(Mockito.anyBoolean());
        BDDMockito.given(channel.isOpen()).willReturn(true);

        return factory;
    }

    @Bean
    TestRabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        return new TestRabbitTemplate(connectionFactory);
    }

    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(final ConnectionFactory connectionFactory) {
        final var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

}
