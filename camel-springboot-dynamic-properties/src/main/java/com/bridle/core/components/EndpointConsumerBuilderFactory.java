package com.bridle.core.components;

import org.apache.camel.builder.EndpointConsumerBuilder;

public interface EndpointConsumerBuilderFactory {
    EndpointConsumerBuilder create(String componentName);

    EndpointConsumerBuilder create();
}
