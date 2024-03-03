package com.github.allisson95.codeflix.application.video.retrieve.list;

import com.github.allisson95.codeflix.application.UseCase;
import com.github.allisson95.codeflix.domain.pagination.Pagination;
import com.github.allisson95.codeflix.domain.video.VideoSearchQuery;

public abstract class ListVideoUseCase
        extends UseCase<VideoSearchQuery, Pagination<VideoListOutput>> {

}
