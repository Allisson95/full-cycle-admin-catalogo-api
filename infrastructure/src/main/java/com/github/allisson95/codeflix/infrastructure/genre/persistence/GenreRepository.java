package com.github.allisson95.codeflix.infrastructure.genre.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GenreRepository
        extends JpaRepository<GenreJpaEntity, String>, JpaSpecificationExecutor<GenreJpaEntity> {

    @Query(value = "SELECT g.id FROM Genre g WHERE g.id IN :ids")
    List<String> existsByIds(@Param("ids") List<String> ids);

}
