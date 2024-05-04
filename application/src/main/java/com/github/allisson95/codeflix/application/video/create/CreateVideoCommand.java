package com.github.allisson95.codeflix.application.video.create;

import java.util.Optional;
import java.util.Set;

import com.github.allisson95.codeflix.domain.resource.Resource;

public record CreateVideoCommand(
        String title,
        String description,
        Integer launchedAt,
        Double duration,
        String rating,
        boolean opened,
        boolean published,
        Set<String> categories,
        Set<String> genres,
        Set<String> castMembers,
        Resource banner,
        Resource thumbnail,
        Resource thumbnailHalf,
        Resource trailer,
        Resource video) {

    public static CreateVideoCommand with(
            final String title,
            final String description,
            final Integer launchedAt,
            final Double duration,
            final String rating,
            final boolean opened,
            final boolean published,
            final Set<String> categories,
            final Set<String> genres,
            final Set<String> castMembers,
            final Resource banner,
            final Resource thumbnail,
            final Resource thumbnailHalf,
            final Resource trailer,
            final Resource video) {
        return new CreateVideoCommand(
                title,
                description,
                launchedAt,
                duration,
                rating,
                opened,
                published,
                categories,
                genres,
                castMembers,
                banner,
                thumbnail,
                thumbnailHalf,
                trailer,
                video);
    }

    public static CreateVideoCommand with(
            final String title,
            final String description,
            final Integer launchedAt,
            final Double duration,
            final String rating,
            final boolean opened,
            final boolean published,
            final Set<String> categories,
            final Set<String> genres,
            final Set<String> castMembers) {
        return new CreateVideoCommand(
                title,
                description,
                launchedAt,
                duration,
                rating,
                opened,
                published,
                categories,
                genres,
                castMembers,
                null,
                null,
                null,
                null,
                null);
    }

    public Optional<Integer> getLaunchedAt() {
        return Optional.ofNullable(launchedAt);
    }

    public Optional<Resource> getBanner() {
        return Optional.ofNullable(banner);
    }

    public Optional<Resource> getThumbnail() {
        return Optional.ofNullable(thumbnail);
    }

    public Optional<Resource> getThumbnailHalf() {
        return Optional.ofNullable(thumbnailHalf);
    }

    public Optional<Resource> getTrailer() {
        return Optional.ofNullable(trailer);
    }

    public Optional<Resource> getVideo() {
        return Optional.ofNullable(video);
    }

}
