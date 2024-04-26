package com.github.allisson95.codeflix.infrastructure.api;

import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping(path = "videos")
@Tag(name = "Video")
public interface VideoAPI {

}
