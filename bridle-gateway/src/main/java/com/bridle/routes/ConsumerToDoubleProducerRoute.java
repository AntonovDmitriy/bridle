package com.bridle.routes;

import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.LoggingLevel;
import org.apache.camel.ValidationException;

import static com.bridle.configuration.common.ComponentNameConstants.REDELIVERY_POLICY;

public class ConsumerToDoubleProducerRoute extends BaseRouteBuilder {

    public static final String PROCESSING_AFTER_CONSUMER = "direct:processingAfterConsumer";

    public static final String PROCESSING_AFTER_PRODUCER = "direct:processingAfterProducer";

    private final ConsumerToDoubleProducerRouteParams routeParams;

    public ConsumerToDoubleProducerRoute(ErrorHandlerFactory errorHandlerFactory,
            ConsumerToDoubleProducerRouteParams routeParams) {
        super(errorHandlerFactory);
        this.routeParams = routeParams;
    }

    @Override
    public void configure() throws Exception {
        super.configure();

        onException(Exception.class).log(LoggingLevel.ERROR, "Exception occurred: ${exception.stacktrace}")
                .redeliveryPolicyRef(REDELIVERY_POLICY)
                .log(LOG_BODY);

        onException(ValidationException.class).log(LoggingLevel.ERROR,
                                                   "Validation exception occurred: ${exception.stacktrace}")
                .log(LOG_BODY);

        ProcessingBuilder.addProcessing(from(PROCESSING_AFTER_CONSUMER),
                                        routeParams.afterConsumerProcessingParams(),
                                        "processingAfterConsumer");
        ProcessingBuilder.addProcessing(from(PROCESSING_AFTER_PRODUCER),
                                        routeParams.afterProducerProcessingParams(),
                                        "processingAfterProducer");

        from(routeParams.consumer()).routeId(routeParams.routeId())
                .to(PROCESSING_AFTER_CONSUMER)
                .to(routeParams.firstProducer())
                .to(PROCESSING_AFTER_PRODUCER)
                .log(LOG_BODY)
                .to(routeParams.secondProducer())
                .log(LOG_BODY);
    }
}
