package com.github.allisson95.codeflix.domain.utils;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CollectionUtils {

    private CollectionUtils() {}

    public static <I, O> Set<O> mapTo(final Set<I> collection, final Function<I, O> mapper) {
        return collection.stream()
                .map(mapper)
                .collect(Collectors.toSet());
    }

}
