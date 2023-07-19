package com.bridle.routes;

import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.commons.lang3.Validate;

public record ConsumerToDoubleProducerRouteParams(String routeId,
                                                  EndpointConsumerBuilder consumer,
                                                  ProcessingParams afterConsumerProcessingParams,
                                                  EndpointProducerBuilder firstProducer,
                                                  ProcessingParams afterProducerProcessingParams,
                                                  EndpointProducerBuilder secondProducer
) {

    public ConsumerToDoubleProducerRouteParams {
        Validate.notEmpty(routeId);
        Validate.notNull(consumer);
        Validate.notNull(firstProducer);
        Validate.notNull(secondProducer);
    }
}
