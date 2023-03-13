package com.github.allisson95.codeflix.infrastructure.category.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository
        extends JpaRepository<CategoryJpaEntity, String>, JpaSpecificationExecutor<CategoryJpaEntity> {

    @Query(value = "SELECT c.id FROM Category c WHERE c.id IN :ids")
    List<String> existsByIds(@Param("ids") List<String> ids);

}
