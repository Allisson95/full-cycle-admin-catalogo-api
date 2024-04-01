package com.github.allisson95.codeflix.infrastructure.castmember.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CastMemberRepository
        extends JpaRepository<CastMemberJpaEntity, String>, JpaSpecificationExecutor<CastMemberJpaEntity> {

    @Query(value = "SELECT c.id FROM CastMember c WHERE c.id IN :ids")
    List<String> existsByIds(@Param("ids") List<String> ids);

}
