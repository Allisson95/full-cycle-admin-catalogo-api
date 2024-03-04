package com.github.allisson95.codeflix.infrastructure.video.persistence;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class VideoCategoryID implements Serializable {

    @Column(name = "video_id", nullable = false)
    private UUID videoId;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    public VideoCategoryID() {
    }

    private VideoCategoryID(final UUID videoId, final UUID categoryId) {
        this.videoId = videoId;
        this.categoryId = categoryId;
    }

    public static VideoCategoryID from(final UUID videoId, final UUID categoryId) {
        return new VideoCategoryID(videoId, categoryId);
    }

    public UUID getVideoId() {
        return videoId;
    }

    public void setVideoId(UUID videoId) {
        this.videoId = videoId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVideoId(), getCategoryId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        VideoCategoryID other = (VideoCategoryID) obj;
        return Objects.equals(getVideoId(), other.getVideoId()) && Objects.equals(getCategoryId(), other.getCategoryId());
    }

}
