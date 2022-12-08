package com.github.allisson95.codeflix.application.category.retrieve.list;

import com.github.allisson95.codeflix.application.UseCase;
import com.github.allisson95.codeflix.domain.pagination.SearchQuery;
import com.github.allisson95.codeflix.domain.pagination.Pagination;

public abstract class ListCategoriesUseCase extends UseCase<SearchQuery, Pagination<CategoryListOutput>> { }
