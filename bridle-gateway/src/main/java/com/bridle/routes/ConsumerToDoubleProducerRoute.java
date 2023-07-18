package com.bridle.routes;

import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.LoggingLevel;
import org.apache.camel.model.RouteDefinition;

import static com.bridle.configuration.common.ComponentNameConstants.REDELIVERY_POLICY;

public class ConsumerToDoubleProducerRoute extends BaseRouteBuilder {

    private final ConsumerToDoubleProducerRouteParams routeParams;

    public ConsumerToDoubleProducerRoute(ErrorHandlerFactory errorHandlerFactory,
                                         ConsumerToDoubleProducerRouteParams routeParams
    ) {
        super(errorHandlerFactory);
        this.routeParams = routeParams;
    }

    @Override
    public void configure() throws Exception {
        super.configure();

        onException(Exception.class)
                .log(LoggingLevel.ERROR, "Exception occurred: ${exception.stacktrace}")
                .handled(true)
                .redeliveryPolicyRef(REDELIVERY_POLICY)
                .log(LOG_BODY);

        RouteDefinition route = from(routeParams.consumer()).routeId(routeParams.routeId());
        ProcessingBuilder.addProcessing(route, routeParams.firstProcessingParams());
        route.to(routeParams.firstProducer());
        ProcessingBuilder.addProcessing(route, routeParams.secondProcessingParams());
        route.log(LOG_BODY).to(routeParams.secondProducer()).log(LOG_BODY);
    }
}
