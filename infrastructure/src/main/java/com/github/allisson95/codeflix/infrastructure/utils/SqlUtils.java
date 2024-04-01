package com.github.allisson95.codeflix.infrastructure.utils;

public final class SqlUtils {

    private SqlUtils() {}

    public static String like(final String term) {
        if (term == null) return null;
        return "%" + term + "%";
    }

    public static String upper(final String term) {
        if (term == null) return null;
        return term.toUpperCase();
    }

}
