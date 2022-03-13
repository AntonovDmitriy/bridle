package com.bridle.core.components.http;

import com.bridle.core.properties.PropertiesLoader;
import org.apache.camel.builder.EndpointProducerBuilder;

import java.util.Map;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.http;

public class SimpleHttpOutEndpointProducerBuilderFactory implements HttpOutFactory {

    public static final String DEFAULT_CONFIG_PROPERTIES_KEY_HTTP_OUT = "http-out";
    private final PropertiesLoader loader;

    public SimpleHttpOutEndpointProducerBuilderFactory(PropertiesLoader loader) {
        this.loader = loader;
    }

    @Override
    public EndpointProducerBuilder create(String componentName) {
        HttpOutProperties properties = loader.load(HttpOutProperties.class, componentName);
        EndpointProducerBuilder component = http(properties.getUrl());
        Map<String, Object> additionalProperties = properties.getAdditional();
        if (additionalProperties != null && !additionalProperties.isEmpty()) {
            additionalProperties.forEach(component::doSetProperty);
        }
        return component;
    }

    @Override
    public EndpointProducerBuilder create() {
        return create(DEFAULT_CONFIG_PROPERTIES_KEY_HTTP_OUT);
    }
}
