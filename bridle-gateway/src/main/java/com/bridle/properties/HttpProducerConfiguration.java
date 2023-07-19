package com.bridle.properties;

import org.apache.camel.component.http.springboot.HttpComponentConfiguration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.util.Map;
import java.util.Optional;

@Validated
public class HttpProducerConfiguration extends HttpComponentConfiguration {
    private String resourcePath = "";

    @NotEmpty
    private String host;

    @Positive
    private Integer port = 80;

    private Map<String, Object> endpointProperties;

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Optional<Map<String, Object>> getEndpointProperties() {
        return Optional.ofNullable(endpointProperties);
    }

    public void setEndpointProperties(Map<String, Object> endpointProperties) {
        this.endpointProperties = endpointProperties;
    }

    public String createHttpUrl() {
        return String.format("%s:%d/%s", host, port, resourcePath);
    }
}
