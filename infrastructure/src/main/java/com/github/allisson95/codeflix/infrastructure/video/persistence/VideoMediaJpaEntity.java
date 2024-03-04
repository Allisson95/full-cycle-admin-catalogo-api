package com.github.allisson95.codeflix.infrastructure.video.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.github.allisson95.codeflix.domain.video.MediaStatus;
import com.github.allisson95.codeflix.domain.video.VideoMedia;

@Entity(name = "VideoMedia")
@Table(name = "videos_video_media")
public class VideoMediaJpaEntity {

    @Id
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "file_path", nullable = false)
    private String rawLocation;

    @Column(name = "encoded_path", nullable = false)
    private String encodedLocation;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaStatus status;

    public VideoMediaJpaEntity() {
    }

    private VideoMediaJpaEntity(
            final String id,
            final String name,
            final String rawLocation,
            final String encodedLocation,
            final MediaStatus status) {
        this.id = id;
        this.name = name;
        this.rawLocation = rawLocation;
        this.encodedLocation = encodedLocation;
        this.status = status;
    }

    public static VideoMediaJpaEntity from(final VideoMedia media) {
        return new VideoMediaJpaEntity(
                media.checksum(),
                media.name(),
                media.rawLocation(),
                media.encodedLocation(),
                media.status());
    }

    public VideoMedia toDomain() {
        return VideoMedia.with(
                getId(),
                getName(),
                getRawLocation(),
                getEncodedLocation(),
                getStatus());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRawLocation() {
        return rawLocation;
    }

    public void setRawLocation(String rawLocation) {
        this.rawLocation = rawLocation;
    }

    public String getEncodedLocation() {
        return encodedLocation;
    }

    public void setEncodedLocation(String encodedLocation) {
        this.encodedLocation = encodedLocation;
    }

    public MediaStatus getStatus() {
        return status;
    }

    public void setStatus(MediaStatus status) {
        this.status = status;
    }

}
