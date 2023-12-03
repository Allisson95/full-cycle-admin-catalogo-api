package com.github.allisson95.codeflix.application.video.retrieve.get;

import java.time.Instant;
import java.util.Set;

import com.github.allisson95.codeflix.domain.Identifier;
import com.github.allisson95.codeflix.domain.utils.CollectionUtils;
import com.github.allisson95.codeflix.domain.video.ImageMedia;
import com.github.allisson95.codeflix.domain.video.Rating;
import com.github.allisson95.codeflix.domain.video.Video;
import com.github.allisson95.codeflix.domain.video.VideoMedia;

public record VideoOutput(
        String id,
        String title,
        String description,
        int launchedAt,
        double duration,
        Rating rating,
        boolean opened,
        boolean published,
        Instant createdAt,
        Instant updatedAt,
        ImageMedia banner,
        ImageMedia thumbnail,
        ImageMedia thumbnailHalf,
        VideoMedia trailer,
        VideoMedia video,
        Set<String> categories,
        Set<String> genres,
        Set<String> castMembers) {

    public static VideoOutput from(final Video aVideo) {
        return new VideoOutput(
                aVideo.getId().getValue(),
                aVideo.getTitle(),
                aVideo.getDescription(),
                aVideo.getLaunchedAt().getValue(),
                aVideo.getDuration(),
                aVideo.getRating(),
                aVideo.isOpened(),
                aVideo.isPublished(),
                aVideo.getCreatedAt(),
                aVideo.getUpdatedAt(),
                aVideo.getBanner().orElse(null),
                aVideo.getThumbnail().orElse(null),
                aVideo.getThumbnailHalf().orElse(null),
                aVideo.getTrailer().orElse(null),
                aVideo.getVideo().orElse(null),
                CollectionUtils.mapTo(aVideo.getCategories(), Identifier::getValue),
                CollectionUtils.mapTo(aVideo.getGenres(), Identifier::getValue),
                CollectionUtils.mapTo(aVideo.getCastMembers(), Identifier::getValue));
    }

}
