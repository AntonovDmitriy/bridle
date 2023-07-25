package com.bridle.routes;

import java.util.Map;

public class EndpointsProperties {

    private Map<String, EndpointProperties> properties;

    public Map<String, EndpointProperties> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, EndpointProperties> properties) {
        this.properties = properties;
    }
}
