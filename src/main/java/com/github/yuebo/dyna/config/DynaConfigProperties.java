package com.github.yuebo.dyna.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "dyna.application")
public class DynaConfigProperties {
    private String applicationName;
    private String copyright;
    private String author;
    public DynaConfigProperties(){
        applicationName="动态表单系统";
        copyright="©Copyright 动态表单2018";
        author="Powered by: www.eappcat.com";
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
