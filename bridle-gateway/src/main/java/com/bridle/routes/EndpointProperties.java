package com.bridle.routes;

import java.util.HashMap;
import java.util.Map;

public class EndpointProperties {

    private String componentName;

    private boolean consumer;

    private Map<String, Object> mandatory = new HashMap<>();

    private Map<String, Object> additional = new HashMap<>();

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public Map<String, Object> getMandatory() {
        return mandatory;
    }

    public void setMandatory(Map<String, Object> mandatory) {
        this.mandatory = mandatory;
    }

    public Map<String, Object> getAdditional() {
        return additional;
    }

    public void setAdditional(Map<String, Object> additional) {
        this.additional = additional;
    }

    public boolean isConsumer() {
        return consumer;
    }

    public void setConsumer(boolean consumer) {
        this.consumer = consumer;
    }
}
