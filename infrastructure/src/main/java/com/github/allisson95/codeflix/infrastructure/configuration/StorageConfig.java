package com.github.allisson95.codeflix.infrastructure.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.github.allisson95.codeflix.infrastructure.configuration.properties.GoogleStorageProperties;
import com.github.allisson95.codeflix.infrastructure.services.StorageService;
import com.github.allisson95.codeflix.infrastructure.services.impl.GCStorageService;
import com.github.allisson95.codeflix.infrastructure.services.local.InMemoryStorageService;
import com.google.cloud.storage.Storage;

@Configuration
public class StorageConfig {

    @Profile({ "development", "production" })
    @Bean(name = "storageService")
    public StorageService gcStorageService(
            final GoogleStorageProperties properties,
            final Storage storage) {
        return new GCStorageService(properties.getBucket(), storage);
    }

    @ConditionalOnMissingBean
    @Bean(name = "storageService")
    public StorageService inMemoryStorageService() {
        return new InMemoryStorageService();
    }

}
