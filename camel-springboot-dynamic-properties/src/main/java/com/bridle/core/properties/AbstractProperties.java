package com.bridle.core.properties;

import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;

import java.util.Map;

public abstract class AbstractProperties {

    private Map<String, Object> additional;

    public Map<String, Object> getAdditional() {
        return additional;
    }

    public void setAdditional(Map<String, Object> additional) {
        this.additional = additional;
    }

    public void fillAdditionalProperties(EndpointConsumerBuilder component) {
        if (additional != null && !additional.isEmpty()) {
            additional.forEach(component::doSetProperty);
        }
    }

    public void fillAdditionalProperties(EndpointProducerBuilder component) {
        if (additional != null && !additional.isEmpty()) {
            additional.forEach(component::doSetProperty);
        }
    }
}
