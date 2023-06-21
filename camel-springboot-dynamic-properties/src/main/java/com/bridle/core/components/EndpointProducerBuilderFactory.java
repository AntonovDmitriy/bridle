package com.bridle.core.components;

import org.apache.camel.builder.EndpointProducerBuilder;

public interface EndpointProducerBuilderFactory {
    EndpointProducerBuilder create(String componentName);
    EndpointProducerBuilder create();
}
