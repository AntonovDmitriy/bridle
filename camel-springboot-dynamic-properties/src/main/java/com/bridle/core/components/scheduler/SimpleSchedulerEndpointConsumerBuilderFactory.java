package com.bridle.core.components.scheduler;

import com.bridle.core.properties.PropertiesLoader;
import org.apache.camel.builder.EndpointConsumerBuilder;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.scheduler;


public class SimpleSchedulerEndpointConsumerBuilderFactory implements SchedulerFactory {

    public static final String DEFAULT_CONFIG_PROPERTIES_KEY_SCHEDULER = "scheduler";

    private final PropertiesLoader propertiesLoader;

    public SimpleSchedulerEndpointConsumerBuilderFactory(PropertiesLoader propertiesLoader) {
        this.propertiesLoader = propertiesLoader;
    }

    @Override
    public EndpointConsumerBuilder create(String componentName) {
        SchedulerProperties properties = propertiesLoader.load(SchedulerProperties.class, componentName);
        EndpointConsumerBuilder component = scheduler("")
                .delay(properties.getDelayMillis())
                .poolSize(properties.getThreadCount());
        properties.fillAdditionalProperties(component);
        return component;
    }

    @Override
    public EndpointConsumerBuilder create() {
        return create(DEFAULT_CONFIG_PROPERTIES_KEY_SCHEDULER);
    }
}
