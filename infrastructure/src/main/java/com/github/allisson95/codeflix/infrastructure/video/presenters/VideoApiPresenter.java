package com.github.allisson95.codeflix.infrastructure.video.presenters;

import com.github.allisson95.codeflix.application.video.media.upload.UploadMediaOutput;
import com.github.allisson95.codeflix.application.video.retrieve.get.VideoOutput;
import com.github.allisson95.codeflix.application.video.retrieve.list.VideoListOutput;
import com.github.allisson95.codeflix.domain.pagination.Pagination;
import com.github.allisson95.codeflix.domain.video.ImageMedia;
import com.github.allisson95.codeflix.domain.video.VideoMedia;
import com.github.allisson95.codeflix.infrastructure.video.models.ImageMediaResponse;
import com.github.allisson95.codeflix.infrastructure.video.models.UploadMediaResponse;
import com.github.allisson95.codeflix.infrastructure.video.models.VideoListResponse;
import com.github.allisson95.codeflix.infrastructure.video.models.VideoMediaResponse;
import com.github.allisson95.codeflix.infrastructure.video.models.VideoResponse;

public interface VideoApiPresenter {

    static VideoResponse present(final VideoOutput output) {
        return new VideoResponse(
                output.id(),
                output.title(),
                output.description(),
                output.launchedAt(),
                output.duration(),
                output.rating().getName(),
                output.opened(),
                output.published(),
                output.createdAt(),
                output.updatedAt(),
                present(output.banner()),
                present(output.thumbnail()),
                present(output.thumbnailHalf()),
                present(output.trailer()),
                present(output.video()),
                output.categories(),
                output.genres(),
                output.castMembers());
    }

    static ImageMediaResponse present(final ImageMedia media) {
        if (media == null) {
            return null;
        }

        return new ImageMediaResponse(
                media.id(),
                media.name(),
                media.location(),
                media.checksum());
    }

    static VideoMediaResponse present(final VideoMedia media) {
        if (media == null) {
            return null;
        }

        return new VideoMediaResponse(
                media.id(),
                media.name(),
                media.checksum(),
                media.rawLocation(),
                media.encodedLocation(),
                media.status().name());
    }

    static VideoListResponse present(final VideoListOutput output) {
        return new VideoListResponse(
                output.id(),
                output.title(),
                output.description(),
                output.createdAt(),
                output.updatedAt());
    }

    static Pagination<VideoListResponse> present(final Pagination<VideoListOutput> page) {
        return page.map(VideoApiPresenter::present);
    }

    static UploadMediaResponse present(final UploadMediaOutput output) {
        return new UploadMediaResponse(output.videoId(), output.mediaType());
    }

}
