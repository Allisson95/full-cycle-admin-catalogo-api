package com.github.allisson95.codeflix.infrastructure.category.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoryRepository extends JpaRepository<CategoryJpaEntity, String>, JpaSpecificationExecutor<CategoryJpaEntity> { }
