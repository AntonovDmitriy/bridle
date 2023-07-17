package com.bridle.routes;

import com.bridle.properties.HttpConsumerConfiguration;
import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.ValidationException;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.model.RouteDefinition;

import static com.bridle.configuration.common.ComponentNameConstants.REDELIVERY_POLICY;
import static com.bridle.configuration.routes.HttpKafkaConfiguration.GATEWAY_TYPE_HTTP_KAFKA;
import static org.apache.camel.component.rest.RestConstants.CONTENT_TYPE;
import static org.apache.camel.component.rest.RestConstants.HTTP_RESPONSE_CODE;

public class HttpKafkaRoute extends GenericHttpConsumerRoute {

    private final EndpointProducerBuilder kafkaOut;
    private final EndpointProducerBuilder successResponseBuilder;
    private final EndpointProducerBuilder errorResponseBuilder;
    private final EndpointProducerBuilder transform;
    private final Processor headerCollector;
    private final DataFormatDefinition beforeTransformDataFormatDefinition;
    private final EndpointProducerBuilder inboundValidator;
    private final EndpointProducerBuilder validationErrorResponseBuilder;

    public HttpKafkaRoute(ErrorHandlerFactory errorHandlerFactory,
                          HttpConsumerConfiguration restConfiguration,
                          EndpointProducerBuilder mainProducer,
                          EndpointProducerBuilder successResponseBuilder,
                          EndpointProducerBuilder errorResponseBuilder,
                          EndpointProducerBuilder transform,
                          Processor headerCollector,
                          DataFormatDefinition beforeTransformDataFormatDefinition,
                          EndpointProducerBuilder inboundValidator,
                          EndpointProducerBuilder validationErrorResponseBuilder) {
        super(errorHandlerFactory, restConfiguration);
        this.kafkaOut = mainProducer;
        this.successResponseBuilder = successResponseBuilder;
        this.errorResponseBuilder = errorResponseBuilder;
        this.transform = transform;
        this.headerCollector = headerCollector;
        this.beforeTransformDataFormatDefinition = beforeTransformDataFormatDefinition;
        this.inboundValidator = inboundValidator;
        this.validationErrorResponseBuilder = validationErrorResponseBuilder;
    }

    @Override
    public void configure() throws Exception {
        super.configure();

        onException(Exception.class)
                .log(LoggingLevel.ERROR, "Exception occurred: ${exception.stacktrace}")
                .handled(true)
                .redeliveryPolicyRef(REDELIVERY_POLICY)
                .setHeader(HTTP_RESPONSE_CODE, constant(restConfiguration.getErrorHttpResponseCode()))
                .to(errorResponseBuilder)
                .log("Response: ${body}");

        onException(ValidationException.class)
                .log(LoggingLevel.ERROR, "Validation exception occurred: ${exception.stacktrace}")
                .handled(true)
                .setHeader(HTTP_RESPONSE_CODE, constant(restConfiguration.getValidationErrorHttpResponseCode()))
                .setHeader(Exchange.EXCEPTION_CAUGHT).exchangeProperty(Exchange.EXCEPTION_CAUGHT)
                .to(validationErrorResponseBuilder)
                .log("Response: ${body}");

        RouteDefinition route = from("direct:process")
                .routeId(GATEWAY_TYPE_HTTP_KAFKA)
                .setHeader(CONTENT_TYPE, constant(restConfiguration.getContentType()))
                .convertBodyTo(String.class);

        if (inboundValidator != null) {
            route.to(inboundValidator);
        }
        route.process(headerCollector);
        if (beforeTransformDataFormatDefinition != null) {
            route.unmarshal(beforeTransformDataFormatDefinition);
        }

        if (transform != null) {
            route.to(transform);
        }

        route.to(kafkaOut)
                .to(successResponseBuilder)
                .log("Response: ${body}");
    }

    // test refactoring
    // next routes
    // info about timings to dashboard
    // load testing with timings every step of route (test jsonpath efficiency)
    // headers filtering
    // default yml with disabling starters
    // journalling
    // kafka headers
    // kafka metrics
}
