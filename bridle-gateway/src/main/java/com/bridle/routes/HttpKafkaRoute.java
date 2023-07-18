package com.bridle.routes;

import com.bridle.configuration.routes.RouteParams;
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

    public static final String LOG_BODY = "Response: ${body}";

    private final RouteParams routeParams;

    public HttpKafkaRoute(ErrorHandlerFactory errorHandlerFactory,
                          HttpConsumerConfiguration restConfiguration,
                          RouteParams routeParams) {
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

        RouteDefinition route = from("direct:process")
                .routeId(GATEWAY_TYPE_HTTP_KAFKA)
                .setHeader(CONTENT_TYPE, constant(restConfiguration.getContentType()))
                .convertBodyTo(String.class);

        if (routeParams.inboundValidator() != null) {
            route.to(routeParams.inboundValidator());
        }
        route.process(routeParams.headerCollector());
        if (routeParams.beforeTransformDataFormatDefinition() != null) {
            route.unmarshal(routeParams.beforeTransformDataFormatDefinition());
        }

        if (routeParams.transform() != null) {
            route.to(routeParams.transform());
        }

        route.to(routeParams.mainProducer())
                .to(routeParams.successResponseBuilder())
                .log(LOG_BODY);
    }

    // next routes
    // info about timings to dashboard
    // load testing with timings every step of route (test jsonpath efficiency)
    // headers filtering
    // default yml with disabling starters
    // journalling
    // kafka headers
    // kafka metrics
}
