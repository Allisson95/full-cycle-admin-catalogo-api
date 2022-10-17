package com.github.allisson95.codeflix.domain.category;

public record CategorySearchQuery(
    int page,
    int perPage,
    String terms,
    String sort,
    String direction
) { }
