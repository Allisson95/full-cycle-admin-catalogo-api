package com.github.allisson95.codeflix.application.castmember.retrieve.list;

import com.github.allisson95.codeflix.application.UseCase;
import com.github.allisson95.codeflix.domain.pagination.Pagination;
import com.github.allisson95.codeflix.domain.pagination.SearchQuery;

public abstract sealed class ListCastMembersUseCase
        extends UseCase<SearchQuery, Pagination<CastMemberListOutput>>
        permits DefaultListCastMembersUseCase {

}
