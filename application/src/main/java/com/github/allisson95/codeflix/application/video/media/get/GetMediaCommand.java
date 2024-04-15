package com.github.allisson95.codeflix.application.video.media.get;

public record GetMediaCommand(
        String videoId,
        String mediaType) {

    public static GetMediaCommand with(final String videoId, final String type) {
        return new GetMediaCommand(videoId, type);
    }

}
