package com.bridle.routes.model;

import com.bridle.utils.ProcessingParams;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.commons.lang3.Validate;

public record ConsumerToProducerRouteParams(String routeId, EndpointConsumerBuilder consumer,
                                            ProcessingParams afterConsumerProcessingParams,
                                            EndpointProducerBuilder firstProducer,
                                            ProcessingParams afterProducerProcessingParams) {

    public ConsumerToProducerRouteParams {
        Validate.notEmpty(routeId);
        Validate.notNull(consumer);
        Validate.notNull(firstProducer);
    }
}
