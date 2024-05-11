package com.github.allisson95.codeflix.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import com.github.allisson95.codeflix.domain.events.DomainEvent;
import com.github.allisson95.codeflix.domain.events.DomainEventPublisher;
import com.github.allisson95.codeflix.domain.utils.IdUtils;
import com.github.allisson95.codeflix.domain.utils.InstantUtils;
import com.github.allisson95.codeflix.domain.validation.ValidationHandler;

class EntityTest extends UnitTest {

    @Test
    void Given_NullAsEvents_When_Instantiate_Should_BeOk() {
        final List<DomainEvent> events = null;

        final var anEntity = new DummyEntity(new DummyID(), events);

        assertNotNull(anEntity);
        assertNotNull(anEntity.getDomainEvents());
        assertTrue(anEntity.getDomainEvents().isEmpty());
    }

    @Test
    void Given_DomainEvents_When_PassToConstructor_Should_CreateAImmutableCloneOfEvents() {
        final List<DomainEvent> events = new ArrayList<>();
        events.add(new DummyEvent());

        final var anEntity = new DummyEntity(new DummyID(), events);

        assertNotNull(anEntity);
        assertNotNull(anEntity.getDomainEvents());
        assertEquals(1, anEntity.getDomainEvents().size());
        assertThrows(RuntimeException.class, () -> {
            final var domainEvents = anEntity.getDomainEvents();
            domainEvents.add(new DummyEvent());
        });
    }

    @Test
    void Given_EmptyDomainEvents_When_RegisterEvent_Should_AddEventToList() {
        final var expectedEvents = 1;

        final var anEntity = new DummyEntity(new DummyID(), new ArrayList<>());

        anEntity.registerEvent(new DummyEvent());

        assertNotNull(anEntity);
        assertNotNull(anEntity.getDomainEvents());
        assertEquals(expectedEvents, anEntity.getDomainEvents().size());
    }

    @Test
    void Given_AFewDomainEvents_When_CallPublishEvents_Should_CallPublisherAndClearEvents() {
        final var expectedEvents = 0;
        final var expectedSentEvents = 2;
        final var counter = new CounterDomainEventPublisher();

        final var anEntity = new DummyEntity(new DummyID(), new ArrayList<>());
        anEntity.registerEvent(new DummyEvent());
        anEntity.registerEvent(new DummyEvent());

        assertEquals(2, anEntity.getDomainEvents().size());

        anEntity.publishDomainEvents(counter);

        assertEquals(expectedEvents, anEntity.getDomainEvents().size());
        assertEquals(expectedSentEvents, counter.getCounter());
    }

    public static class CounterDomainEventPublisher implements DomainEventPublisher {

        private final AtomicInteger counter;

        public CounterDomainEventPublisher() {
            this.counter = new AtomicInteger(0);
        }

        @Override
        public void publishEvent(final DomainEvent event) {
            counter.incrementAndGet();
        }

        public int getCounter() {
            return counter.get();
        }

    }

    public static class DummyEvent implements DomainEvent {

        private final Instant occurredOn;

        public DummyEvent() {
            this.occurredOn = InstantUtils.now();
        }

        @Override
        public Instant occurredOn() {
            return this.occurredOn;
        }

    }

    public static class DummyID extends Identifier {

        private final String id;

        public DummyID() {
            this.id = IdUtils.uuid();
        }

        @Override
        public String getValue() {
            return this.id;
        }

    }

    public static class DummyEntity extends Entity<DummyID> {

        public DummyEntity(final DummyID id, final List<DomainEvent> domainEvents) {
            super(id, domainEvents);
        }

        @Override
        public void validate(final ValidationHandler aHandler) {

        }

    }

}
