package com.bridle.core.components.kafka;

import com.bridle.core.properties.PropertiesLoader;
import org.apache.camel.builder.EndpointProducerBuilder;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.kafka;

public class SimpleKafkaOutEndpointProducerBuilderFactory implements KafkaOutFactory {

    public static final String DEFAULT_CONFIG_PROPERTIES_KEY_KAFKA_OUT = "kafka-out";

    private final PropertiesLoader loader;

    public SimpleKafkaOutEndpointProducerBuilderFactory(PropertiesLoader loader) {
        this.loader = loader;
    }

    @Override
    public EndpointProducerBuilder create(String componentName) {
        KafkaOutProperties properties = loader.load(KafkaOutProperties.class, componentName);
        EndpointProducerBuilder component = kafka(properties.getTopic())
                .brokers(properties.getBrokers());
        properties.fillAdditionalProperties(component);
        return component;
    }

    @Override
    public EndpointProducerBuilder create() {
        return create(DEFAULT_CONFIG_PROPERTIES_KEY_KAFKA_OUT);
    }
}
