package com.bridle.routes;

import com.bridle.properties.HttpConsumerConfiguration;
import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.ValidationException;

import static com.bridle.configuration.common.ComponentNameConstants.REDELIVERY_POLICY;
import static org.apache.camel.component.rest.RestConstants.CONTENT_TYPE;
import static org.apache.camel.component.rest.RestConstants.HTTP_RESPONSE_CODE;

public class HttpConsumerToProducerRoute extends GenericHttpConsumerRoute {

    public static final String PROCESSING_AFTER_CONSUMER = "direct:processingAfterConsumer";

    public static final String PROCESSING_AFTER_PRODUCER = "direct:processingAfterProducer";
    private final HttpConsumerToProducerRouteParams routeParams;


    public HttpConsumerToProducerRoute(ErrorHandlerFactory errorHandlerFactory,
                                       HttpConsumerConfiguration restConfiguration,
                                       HttpConsumerToProducerRouteParams routeParams) {
        super(errorHandlerFactory, restConfiguration);
        this.routeParams = routeParams;
    }

    @Override
    public void configure() throws Exception {
        super.configure();

        onException(Exception.class)
                .log(LoggingLevel.ERROR, "Exception occurred: ${exception.stacktrace}")
                .handled(true)
                .redeliveryPolicyRef(REDELIVERY_POLICY)
                .setHeader(HTTP_RESPONSE_CODE, constant(restConfiguration.getErrorHttpResponseCode()))
                .to(routeParams.errorResponseBuilder())
                .log(LOG_BODY);

        onException(ValidationException.class)
                .log(LoggingLevel.ERROR, "Validation exception occurred: ${exception.stacktrace}")
                .handled(true)
                .setHeader(HTTP_RESPONSE_CODE, constant(restConfiguration.getValidationErrorHttpResponseCode()))
                .setHeader(Exchange.EXCEPTION_CAUGHT).exchangeProperty(Exchange.EXCEPTION_CAUGHT)
                .to(routeParams.validationErrorResponseBuilder())
                .log(LOG_BODY);

        ProcessingBuilder.addProcessing(from(PROCESSING_AFTER_CONSUMER),
                routeParams.afterConsumerProcessingParams(), "processingAfterConsumer");
        ProcessingBuilder.addProcessing(from(PROCESSING_AFTER_PRODUCER),
                routeParams.afterProducerProcessingParams(), "processingAfterProducer");

        from("direct:process")
            .routeId(routeParams.routeId())
            .setHeader(CONTENT_TYPE, constant(restConfiguration.getContentType()))
            .to(PROCESSING_AFTER_CONSUMER)
            .to(routeParams.mainProducer())
            .to(PROCESSING_AFTER_PRODUCER)
            .to(routeParams.successResponseBuilder())
            .log(LOG_BODY);
    }
}
