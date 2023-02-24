package com.github.allisson95.codeflix.infrastructure.genre.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GenreRepository
        extends JpaRepository<GenreJpaEntity, String>, JpaSpecificationExecutor<GenreJpaEntity> {

}
