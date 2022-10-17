package com.github.allisson95.codeflix.domain.pagination;

import java.util.List;

public record Pagination<T> (
    int currentPage,
    int perPage,
    long total,
    List<T> items
) { }
