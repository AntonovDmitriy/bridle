package com.bridle.configuration.common;

import com.bridle.properties.FreemarkerProducerConfiguration;
import com.bridle.properties.ValidatedKafkaProducerConfiguration;
import org.apache.camel.builder.EndpointProducerBuilder;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.freemarker;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.kafka;

public class EndpointBuilders {

    public static EndpointProducerBuilder buildKafkaProducer(String componentName,
                                                             ValidatedKafkaProducerConfiguration kafkaOutConfiguration) {
        EndpointProducerBuilder result = kafka(componentName, kafkaOutConfiguration.getTopic());
        kafkaOutConfiguration.getEndpointProperties()
                .ifPresent(additional -> additional.forEach(result::doSetProperty));
        return result;
    }

    public static EndpointProducerBuilder buildFreemarker(String componentName,
                                                          FreemarkerProducerConfiguration freemarkerProducerConfiguration) {
        EndpointProducerBuilder result = freemarker(componentName, freemarkerProducerConfiguration.getResourceUri());
        freemarkerProducerConfiguration.getEndpointProperties()
                .ifPresent(additional -> additional.forEach(result::doSetProperty));
        return result;
    }

}
