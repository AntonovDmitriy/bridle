package com.bridle.routes;

import org.apache.camel.Processor;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.model.ConvertBodyDefinition;
import org.apache.camel.model.DataFormatDefinition;

public record ProcessingParams(
        EndpointProducerBuilder validator,
        ConvertBodyDefinition convertBody,
        Processor headerCollector,
        DataFormatDefinition beforeTransform,
        EndpointProducerBuilder transform,
        DataFormatDefinition afterTransform) {
}
