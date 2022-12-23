package com.github.allisson95.codeflix.application.genre.update;

import java.util.ArrayList;
import java.util.List;

public record UpdateGenreCommand(
        String id,
        String name,
        boolean isActive,
        List<String> categories) {

    public static UpdateGenreCommand with(
            final String anId,
            final String aName,
            final Boolean isActive,
            final List<String> categories) {
        return new UpdateGenreCommand(anId,
                aName,
                (isActive == null) || isActive,
                categories != null ? categories : new ArrayList<>());
    }

}
