package com.bridle.configuration.routes;

import org.apache.camel.Processor;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.model.ConvertBodyDefinition;
import org.apache.camel.model.DataFormatDefinition;
import org.apache.commons.lang3.Validate;

public record RouteParams(String routeId,
                          EndpointProducerBuilder mainProducer,
                          EndpointProducerBuilder successResponseBuilder,
                          EndpointProducerBuilder errorResponseBuilder,
                          EndpointProducerBuilder transform,
                          Processor headerCollector,
                          DataFormatDefinition beforeTransformDataFormatDefinition,
                          DataFormatDefinition afterTransformDataFormatDefinition,
                          EndpointProducerBuilder inboundValidator,
                          EndpointProducerBuilder validationErrorResponseBuilder,
                          ConvertBodyDefinition convertBody) {

    public RouteParams {
        Validate.notEmpty(routeId);
        Validate.notNull(mainProducer);
        Validate.notNull(successResponseBuilder);
        Validate.notNull(headerCollector);
    }
}
