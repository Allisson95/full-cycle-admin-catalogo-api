package com.github.allisson95.codeflix.domain.events;

@FunctionalInterface
public interface DomainEventPublisher {

    void publishEvent(DomainEvent event);

}
