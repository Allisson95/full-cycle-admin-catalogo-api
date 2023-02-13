package com.github.allisson95.codeflix.application.genre.retrieve.list;

import com.github.allisson95.codeflix.application.UseCase;
import com.github.allisson95.codeflix.domain.pagination.Pagination;
import com.github.allisson95.codeflix.domain.pagination.SearchQuery;

public abstract class ListGenreUseCase extends UseCase<SearchQuery, Pagination<GenreListOutput>> {

}
