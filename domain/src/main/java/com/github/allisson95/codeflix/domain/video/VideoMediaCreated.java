package com.github.allisson95.codeflix.domain.video;

import java.time.Instant;

import com.github.allisson95.codeflix.domain.events.DomainEvent;
import com.github.allisson95.codeflix.domain.utils.InstantUtils;

public record VideoMediaCreated(
        String resourceId,
        String filePath,
        Instant occurredOn) implements DomainEvent {

    public VideoMediaCreated(final String resourceId, final String filePath) {
        this(resourceId, filePath, InstantUtils.now());
    }

}
