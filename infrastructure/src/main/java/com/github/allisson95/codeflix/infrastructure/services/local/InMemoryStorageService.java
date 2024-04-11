package com.github.allisson95.codeflix.infrastructure.services.local;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.github.allisson95.codeflix.domain.resource.Resource;
import com.github.allisson95.codeflix.infrastructure.services.StorageService;

public class InMemoryStorageService implements StorageService {

    private final Map<String, Resource> storage;

    public InMemoryStorageService() {
        this.storage = new ConcurrentHashMap<>();
    }

    public Map<String, Resource> storage() {
        return this.storage;
    }

    public void reset() {
        this.storage.clear();
    }

    @Override
    public void deleteAll(final Collection<String> names) {
        names.forEach(this.storage::remove);
    }

    @Override
    public Optional<Resource> get(final String name) {
        return Optional.ofNullable(this.storage.get(name));
    }

    @Override
    public List<String> list(final String prefix) {
        if (prefix == null) {
            return Collections.emptyList();
        }

        return this.storage.keySet().stream()
                .filter(it -> it.startsWith(prefix))
                .toList();
    }

    @Override
    public void store(final String name, final Resource resource) {
        this.storage.put(name, resource);
    }

}
