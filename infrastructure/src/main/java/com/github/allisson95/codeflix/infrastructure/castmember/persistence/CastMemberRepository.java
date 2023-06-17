package com.github.allisson95.codeflix.infrastructure.castmember.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CastMemberRepository
        extends JpaRepository<CastMemberJpaEntity, String>, JpaSpecificationExecutor<CastMemberJpaEntity> {

}
