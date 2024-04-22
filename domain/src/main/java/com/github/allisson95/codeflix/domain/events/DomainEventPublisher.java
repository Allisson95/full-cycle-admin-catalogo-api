package com.github.allisson95.codeflix.domain.events;

@FunctionalInterface
public interface DomainEventPublisher<T extends DomainEvent> {

    void publishEvent(T event);

}
