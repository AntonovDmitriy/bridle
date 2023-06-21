package com.bridle.utils;

import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.spi.ComponentCustomizer;
import org.apache.camel.spring.boot.util.CamelPropertiesHelper;

import java.util.Objects;

public class ComponentCustomizerImpl implements ComponentCustomizer {
    private final CamelContext context;

    private final Object propertySource;

    private final String componentName;

    public ComponentCustomizerImpl(CamelContext context, Object propertySource, String componentName) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(propertySource);
        Objects.requireNonNull(componentName);

        this.context = context;
        this.propertySource = propertySource;
        this.componentName = componentName;
    }

    @Override
    public void configure(String name, Component target) {
        CamelPropertiesHelper.copyProperties(context, propertySource, target);
    }

    @Override
    public boolean isEnabled(String name, Component target) {
        return componentName.equals(name);
    }
}
