package com.github.allisson95.codeflix.application.genre.create;

import java.util.ArrayList;
import java.util.List;

public record CreateGenreCommand(
        String name,
        boolean active,
        List<String> categories) {

    public static CreateGenreCommand with(
            final String aName,
            final Boolean isActive,
            final List<String> categories) {
        return new CreateGenreCommand(
                aName,
                (isActive == null) || isActive,
                categories != null ? categories : new ArrayList<>());
    }

}
