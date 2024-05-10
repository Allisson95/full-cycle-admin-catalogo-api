package com.github.allisson95.codeflix.infrastructure.configuration.usecases;

import java.util.Objects;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.allisson95.codeflix.application.video.create.CreateVideoUseCase;
import com.github.allisson95.codeflix.application.video.create.DefaultCreateVideoUseCase;
import com.github.allisson95.codeflix.application.video.delete.DefaultDeleteVideoUseCase;
import com.github.allisson95.codeflix.application.video.delete.DeleteVideoUseCase;
import com.github.allisson95.codeflix.application.video.media.get.DefaultGetMediaUseCase;
import com.github.allisson95.codeflix.application.video.media.get.GetMediaUseCase;
import com.github.allisson95.codeflix.application.video.media.update.DefaultUpdateMediaStatusUseCase;
import com.github.allisson95.codeflix.application.video.media.update.UpdateMediaStatusUseCase;
import com.github.allisson95.codeflix.application.video.media.upload.DefaultUploadMediaUseCase;
import com.github.allisson95.codeflix.application.video.media.upload.UploadMediaUseCase;
import com.github.allisson95.codeflix.application.video.retrieve.get.DefaultGetVideoByIdUseCase;
import com.github.allisson95.codeflix.application.video.retrieve.get.GetVideoByIdUseCase;
import com.github.allisson95.codeflix.application.video.retrieve.list.DefaultListVideoUseCase;
import com.github.allisson95.codeflix.application.video.retrieve.list.ListVideoUseCase;
import com.github.allisson95.codeflix.application.video.update.DefaultUpdateVideoUseCase;
import com.github.allisson95.codeflix.application.video.update.UpdateVideoUseCase;
import com.github.allisson95.codeflix.domain.castmember.CastMemberGateway;
import com.github.allisson95.codeflix.domain.category.CategoryGateway;
import com.github.allisson95.codeflix.domain.genre.GenreGateway;
import com.github.allisson95.codeflix.domain.video.MediaResourceGateway;
import com.github.allisson95.codeflix.domain.video.VideoGateway;

@Configuration
public class VideoUseCaseConfig {

    private final CategoryGateway categoryGateway;
    private final CastMemberGateway castMemberGateway;
    private final GenreGateway genreGateway;
    private final MediaResourceGateway mediaResourceGateway;
    private final VideoGateway videoGateway;

    public VideoUseCaseConfig(
            final CategoryGateway categoryGateway,
            final CastMemberGateway castMemberGateway,
            final GenreGateway genreGateway,
            final MediaResourceGateway mediaResourceGateway,
            final VideoGateway videoGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
        this.genreGateway = Objects.requireNonNull(genreGateway);
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Bean
    public CreateVideoUseCase createVideoUseCase() {
        return new DefaultCreateVideoUseCase(
                videoGateway,
                mediaResourceGateway,
                categoryGateway,
                castMemberGateway,
                genreGateway);
    }

    @Bean
    public GetVideoByIdUseCase getVideoByIdUseCase() {
        return new DefaultGetVideoByIdUseCase(videoGateway);
    }

    @Bean
    public UpdateVideoUseCase updateVideoUseCase() {
        return new DefaultUpdateVideoUseCase(
                videoGateway,
                mediaResourceGateway,
                categoryGateway,
                castMemberGateway,
                genreGateway);
    }

    @Bean
    public DeleteVideoUseCase deleteVideoUseCase() {
        return new DefaultDeleteVideoUseCase(videoGateway, mediaResourceGateway);
    }

    @Bean
    public ListVideoUseCase listVideoUseCase() {
        return new DefaultListVideoUseCase(videoGateway);
    }

    @Bean
    public GetMediaUseCase getMediaUseCase() {
        return new DefaultGetMediaUseCase(mediaResourceGateway);
    }

    @Bean
    public UploadMediaUseCase uploadMediaUseCase() {
        return new DefaultUploadMediaUseCase(videoGateway, mediaResourceGateway);
    }

    @Bean
    public UpdateMediaStatusUseCase updateMediaStatusUseCase() {
        return new DefaultUpdateMediaStatusUseCase(videoGateway);
    }

}
