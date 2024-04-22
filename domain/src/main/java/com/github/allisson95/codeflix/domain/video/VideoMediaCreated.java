package com.github.allisson95.codeflix.domain.video;

import java.time.Instant;

import com.github.allisson95.codeflix.domain.events.DomainEvent;
import com.github.allisson95.codeflix.domain.utils.InstantUtils;

public class VideoMediaCreated implements DomainEvent {

    private final Instant occurredOn;
    private final String resourceId;
    private final String filePath;

    public VideoMediaCreated(final String resourceId, final String filePath) {
        this.occurredOn = InstantUtils.now();
        this.resourceId = resourceId;
        this.filePath = filePath;
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }

    public String resourceId() {
        return resourceId;
    }

    public String filePath() {
        return filePath;
    }

}
