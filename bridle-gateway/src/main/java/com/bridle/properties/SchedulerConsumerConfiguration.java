package com.bridle.properties;

import org.apache.camel.component.scheduler.springboot.SchedulerComponentConfiguration;

import java.util.Map;
import java.util.Optional;

public class SchedulerConsumerConfiguration extends SchedulerComponentConfiguration {

    private Map<String, Object> endpointProperties;

    public Optional<Map<String, Object>> getEndpointProperties() {
        return Optional.ofNullable(endpointProperties);
    }

    public void setEndpointProperties(Map<String, Object> endpointProperties) {
        this.endpointProperties = endpointProperties;
    }
}
