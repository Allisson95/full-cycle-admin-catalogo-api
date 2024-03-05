package com.github.allisson95.codeflix.infrastructure.video.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<VideoJpaEntity, UUID> {
}
