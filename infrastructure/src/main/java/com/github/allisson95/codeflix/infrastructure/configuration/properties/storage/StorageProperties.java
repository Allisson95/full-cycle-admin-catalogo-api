package com.github.allisson95.codeflix.infrastructure.configuration.properties.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class StorageProperties implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(StorageProperties.class);

    private String filenamePattern;
    private String locationPattern;

    public String getFilenamePattern() {
        return filenamePattern;
    }

    public void setFilenamePattern(String filenamePattern) {
        this.filenamePattern = filenamePattern;
    }

    public String getLocationPattern() {
        return locationPattern;
    }

    public void setLocationPattern(String locationPattern) {
        this.locationPattern = locationPattern;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.debug("""

                filename-pattern: {}
                location-pattern: {}
                    """,
                getFilenamePattern(),
                getLocationPattern());
    }

}
