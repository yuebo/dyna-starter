package com.ifreelight.dyna.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "json")
public class JsonConfigProperties {
    private String path="~/upload";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
