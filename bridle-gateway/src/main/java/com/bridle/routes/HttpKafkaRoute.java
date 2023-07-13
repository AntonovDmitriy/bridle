package com.bridle.routes;

import com.bridle.properties.HttpConsumerConfiguration;
import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.builder.DefaultErrorHandlerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;

import static com.bridle.configuration.routes.HttpKafkaConfiguration.GATEWAY_TYPE_HTTP_KAFKA;
import static org.apache.camel.component.rest.RestConstants.CONTENT_TYPE;
import static org.apache.camel.component.rest.RestConstants.HTTP_RESPONSE_CODE;

public class HttpKafkaRoute extends GenericHttpConsumerRoute {

    private final EndpointProducerBuilder kafkaOut;
    private final EndpointProducerBuilder successResponseBuilder;
    private final EndpointProducerBuilder errorResponseBuilder;

    public HttpKafkaRoute(ErrorHandlerFactory errorHandlerFactory,
                          HttpConsumerConfiguration restConfiguration,
                          EndpointProducerBuilder kafkaOut,
                          EndpointProducerBuilder successResponseBuilder,
                          EndpointProducerBuilder errorResponseBuilder) {
        super(errorHandlerFactory, restConfiguration);
        this.kafkaOut = kafkaOut;
        this.successResponseBuilder = successResponseBuilder;
        this.errorResponseBuilder = errorResponseBuilder;
    }

    @Override
    public void configure() throws Exception {
        super.configure();

        errorHandler(deadLetterChannel("log:com.mycompany.order?level=DEBUG").maximumRedeliveries(2));

        onException(Exception.class)
                .setHeader(HTTP_RESPONSE_CODE, constant(restConfiguration.getErrorHttpResponseCode()))
                .to(errorResponseBuilder)
                .log("Response: ${body}");

        from("direct:process")
            .routeId(GATEWAY_TYPE_HTTP_KAFKA)
            .setHeader(CONTENT_TYPE, constant(restConfiguration.getContentType()))
            .to(kafkaOut)
            .to(successResponseBuilder)
            .log("Response: ${body}");
    }

    // error message to client
    // check metrics error
    // validation
    // transformation
    // error transformation
    // journalling
    // kafka headers
    // kafka metrics
}
