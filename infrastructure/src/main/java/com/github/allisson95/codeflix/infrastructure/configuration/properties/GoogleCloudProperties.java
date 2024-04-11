package com.github.allisson95.codeflix.infrastructure.configuration.properties;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class GoogleCloudProperties implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(GoogleCloudProperties.class);

    private String credentials;
    private String projectId;

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.logProperties();
    }

    private void logProperties() {
        if (logger.isDebugEnabled()) {
            final var credentialsPart = Optional.ofNullable(getCredentials())
                    .map(it -> it.substring(0, 9)
                            .concat("..."))
                    .orElse("null");
            logger.debug("""

                    credentials: {}
                    project-id: {}
                    """,
                    credentialsPart,
                    getProjectId());
        }
    }

}
