package com.bridle.properties;

import org.apache.camel.component.freemarker.springboot.FreemarkerComponentConfiguration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.Map;
import java.util.Optional;

@Validated
public class FreemarkerProducerConfiguration extends FreemarkerComponentConfiguration {

    private String resourceUri;

    private Map<String, Object> endpointProperties;

    public Optional<Map<String, Object>> getEndpointProperties() {
        return Optional.ofNullable(endpointProperties);
    }

    public void setEndpointProperties(Map<String, Object> endpointProperties) {
        this.endpointProperties = endpointProperties;
    }

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }
}
