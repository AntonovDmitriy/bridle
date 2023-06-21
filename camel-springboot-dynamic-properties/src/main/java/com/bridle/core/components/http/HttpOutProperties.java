package com.bridle.core.components.http;

import com.bridle.core.properties.AbstractProperties;

import javax.validation.constraints.NotEmpty;

public class HttpOutProperties extends AbstractProperties {

    @NotEmpty
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
