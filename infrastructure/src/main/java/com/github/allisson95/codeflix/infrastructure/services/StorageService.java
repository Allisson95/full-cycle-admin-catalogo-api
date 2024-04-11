package com.github.allisson95.codeflix.infrastructure.services;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.github.allisson95.codeflix.domain.video.Resource;

public interface StorageService {

    void deleteAll(Collection<String> names);

    Optional<Resource> get(String name);

    List<String> list(String prefix);

    void store(String name, Resource resource);

}
