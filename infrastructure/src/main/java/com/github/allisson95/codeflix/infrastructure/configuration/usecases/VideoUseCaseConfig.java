package com.github.allisson95.codeflix.infrastructure.configuration.usecases;

import java.util.Objects;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.allisson95.codeflix.application.video.media.update.DefaultUpdateMediaStatusUseCase;
import com.github.allisson95.codeflix.application.video.media.update.UpdateMediaStatusUseCase;
import com.github.allisson95.codeflix.domain.video.VideoGateway;

@Configuration
public class VideoUseCaseConfig {

    private final VideoGateway videoGateway;

    public VideoUseCaseConfig(final VideoGateway videoGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Bean
    public UpdateMediaStatusUseCase updateMediaStatusUseCase() {
        return new DefaultUpdateMediaStatusUseCase(videoGateway);
    }

}
