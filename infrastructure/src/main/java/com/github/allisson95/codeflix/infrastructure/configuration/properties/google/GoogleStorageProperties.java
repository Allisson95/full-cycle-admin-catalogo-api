package com.github.allisson95.codeflix.infrastructure.configuration.properties.google;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class GoogleStorageProperties implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(GoogleStorageProperties.class);

    private String bucket;
    private int connectTimeout;
    private int readTimeout;
    private int retryDelay;
    private int retryMaxAttempts;
    private int retryMaxDelay;
    private double retryMultiplier;

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(int retryDelay) {
        this.retryDelay = retryDelay;
    }

    public int getRetryMaxAttempts() {
        return retryMaxAttempts;
    }

    public void setRetryMaxAttempts(int retryMaxAttempts) {
        this.retryMaxAttempts = retryMaxAttempts;
    }

    public int getRetryMaxDelay() {
        return retryMaxDelay;
    }

    public void setRetryMaxDelay(int retryMaxDelay) {
        this.retryMaxDelay = retryMaxDelay;
    }

    public double getRetryMultiplier() {
        return retryMultiplier;
    }

    public void setRetryMultiplier(double retryMultiplier) {
        this.retryMultiplier = retryMultiplier;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.logProperties();
    }

    private void logProperties() {
        logger.debug("""

                bucket: {}
                connect-timeout: {}
                read-timeout: {}
                retry-delay: {}
                retry-max-attempts: {}
                retry-max-delay: {}
                retry-multiplier: {}
                    """,
                getBucket(),
                getConnectTimeout(),
                getReadTimeout(),
                getRetryDelay(),
                getRetryMaxAttempts(),
                getRetryMaxDelay(),
                getRetryMultiplier());
    }

}
