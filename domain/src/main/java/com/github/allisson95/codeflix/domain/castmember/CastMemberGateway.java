package com.github.allisson95.codeflix.domain.castmember;

import java.util.List;
import java.util.Optional;

import com.github.allisson95.codeflix.domain.pagination.Pagination;
import com.github.allisson95.codeflix.domain.pagination.SearchQuery;

public interface CastMemberGateway {

    CastMember create(CastMember aCastMember);

    void deleteById(CastMemberID anId);

    Optional<CastMember> findById(CastMemberID anId);

    Pagination<CastMember> findAll(SearchQuery aQuery);

    CastMember update(CastMember aCastMember);

    List<CastMemberID> existsByIds(Iterable<CastMemberID> ids);

}
