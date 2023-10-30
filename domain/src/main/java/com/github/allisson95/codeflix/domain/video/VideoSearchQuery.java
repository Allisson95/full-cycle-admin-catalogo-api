package com.github.allisson95.codeflix.domain.video;

public record VideoSearchQuery(
        int page,
        int perPage,
        String terms,
        String sort,
        String direction) {
}
