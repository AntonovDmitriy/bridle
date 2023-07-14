package com.bridle.routes;

import com.bridle.properties.HttpConsumerConfiguration;
import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.Processor;
import org.apache.camel.builder.EndpointProducerBuilder;

import static com.bridle.configuration.common.ComponentNameConstants.REDELIVERY_POLICY;
import static com.bridle.configuration.routes.HttpKafkaConfiguration.GATEWAY_TYPE_HTTP_KAFKA;
import static org.apache.camel.component.rest.RestConstants.CONTENT_TYPE;
import static org.apache.camel.component.rest.RestConstants.HTTP_RESPONSE_CODE;

public class HttpKafkaRoute extends GenericHttpConsumerRoute {

    private final EndpointProducerBuilder kafkaOut;
    private final EndpointProducerBuilder successResponseBuilder;
    private final EndpointProducerBuilder errorResponseBuilder;
    private final Processor headerCollector;

    public HttpKafkaRoute(ErrorHandlerFactory errorHandlerFactory,
                          HttpConsumerConfiguration restConfiguration,
                          EndpointProducerBuilder kafkaOut,
                          EndpointProducerBuilder successResponseBuilder,
                          EndpointProducerBuilder errorResponseBuilder, Processor headerCollector) {
        super(errorHandlerFactory, restConfiguration);
        this.kafkaOut = kafkaOut;
        this.successResponseBuilder = successResponseBuilder;
        this.errorResponseBuilder = errorResponseBuilder;
        this.headerCollector = headerCollector;
    }

    @Override
    public void configure() throws Exception {
        super.configure();

        onException(Exception.class)
                .handled(true)
                .redeliveryPolicyRef(REDELIVERY_POLICY)
                .setHeader(HTTP_RESPONSE_CODE, constant(restConfiguration.getErrorHttpResponseCode()))
                .to(errorResponseBuilder)
                .log("Response: ${body}");

        from("direct:process")
            .routeId(GATEWAY_TYPE_HTTP_KAFKA)
            .setHeader(CONTENT_TYPE, constant(restConfiguration.getContentType()))
            .process(headerCollector)
            .to(kafkaOut)
            .to(successResponseBuilder)
            .log("Response: ${body}");
    }

    // metrics test
    // header collector
    // default template for error and response
    // test read messages from file
    // idea to decide marshalling or header collector
    // marshalling example
    // check metrics error
    // validation
    // transformation
    // error transformation
    // journalling
    // kafka headers
    // kafka metrics
}
