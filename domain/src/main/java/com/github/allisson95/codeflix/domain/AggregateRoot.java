package com.github.allisson95.codeflix.domain;

import java.util.Collections;
import java.util.List;

import com.github.allisson95.codeflix.domain.events.DomainEvent;

public abstract class AggregateRoot<ID extends Identifier> extends Entity<ID> {

    protected AggregateRoot(final ID id) {
        this(id, Collections.emptyList());
    }

    protected AggregateRoot(final ID id, final List<DomainEvent> domainEvents) {
        super(id, domainEvents);
    }

}
