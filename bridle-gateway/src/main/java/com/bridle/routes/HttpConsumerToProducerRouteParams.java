package com.bridle.routes;

import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.commons.lang3.Validate;

public record HttpConsumerToProducerRouteParams(String routeId, EndpointProducerBuilder mainProducer,
                                                EndpointProducerBuilder successResponseBuilder,
                                                EndpointProducerBuilder errorResponseBuilder,
                                                EndpointProducerBuilder validationErrorResponseBuilder,
                                                ProcessingParams afterConsumerProcessingParams,
                                                ProcessingParams afterProducerProcessingParams) {
    public HttpConsumerToProducerRouteParams {
        Validate.notEmpty(routeId);
        Validate.notNull(mainProducer);
        Validate.notNull(successResponseBuilder);
    }
}
