package com.bridle.properties;

import org.apache.camel.component.jsonvalidator.springboot.JsonValidatorComponentConfiguration;

import java.util.Map;
import java.util.Optional;

public class JsonSchemaValidatorConfiguration extends JsonValidatorComponentConfiguration {

    private Map<String, Object> endpointProperties;
    private String resourceUri;

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
