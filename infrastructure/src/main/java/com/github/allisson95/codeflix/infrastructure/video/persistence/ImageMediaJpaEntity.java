package com.github.allisson95.codeflix.infrastructure.video.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.github.allisson95.codeflix.domain.video.ImageMedia;

@Table(name = "videos_image_media")
@Entity(name = "ImageMedia")
public class ImageMediaJpaEntity {

    @Id
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "file_path", nullable = false)
    private String location;

    public ImageMediaJpaEntity() {
    }

    private ImageMediaJpaEntity(
            final String id,
            final String name,
            final String rawLocation) {
        this.id = id;
        this.name = name;
        this.location = rawLocation;
    }

    public static ImageMediaJpaEntity from(final ImageMedia media) {
        return new ImageMediaJpaEntity(media.checksum(), media.name(), media.location());
    }

    public ImageMedia toDomain() {
        return ImageMedia.with(getId(), getName(), getLocation());
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String rawLocation) {
        this.location = rawLocation;
    }

}
