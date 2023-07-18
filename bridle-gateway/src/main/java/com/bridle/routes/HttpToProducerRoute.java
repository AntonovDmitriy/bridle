package com.bridle.routes;

import com.bridle.configuration.routes.RouteParams;
import com.bridle.properties.HttpConsumerConfiguration;
import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.ValidationException;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.model.ConvertBodyDefinition;
import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.model.RouteDefinition;

import static com.bridle.configuration.common.ComponentNameConstants.REDELIVERY_POLICY;
import static com.bridle.configuration.routes.HttpKafkaConfiguration.GATEWAY_TYPE_HTTP_KAFKA;
import static org.apache.camel.component.rest.RestConstants.CONTENT_TYPE;
import static org.apache.camel.component.rest.RestConstants.HTTP_RESPONSE_CODE;

public class HttpToProducerRoute extends GenericHttpConsumerRoute {

    public static final String LOG_BODY = "Response: ${body}";
    private final RouteParams routeParams;


    public HttpToProducerRoute(ErrorHandlerFactory errorHandlerFactory,
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

        RouteDefinition routeDefinition = from("direct:process")
                .routeId(GATEWAY_TYPE_HTTP_KAFKA)
                .setHeader(CONTENT_TYPE, constant(restConfiguration.getContentType()));

        if (routeParams.convertBody() != null) {
            routeDefinition.addOutput(routeParams.convertBody());
        }

        if (routeParams.inboundValidator() != null) {
            routeDefinition.to(routeParams.inboundValidator());
        }

        if (routeParams.headerCollector() != null) {
            routeDefinition.process(routeParams.headerCollector());
        }

        if (routeParams.beforeTransformDataFormatDefinition() != null) {
            routeDefinition.unmarshal(routeParams.beforeTransformDataFormatDefinition());
        }

        if (routeParams.transform() != null) {
            routeDefinition.to(routeParams.transform());
        }

        if (routeParams.afterTransformDataFormatDefinition() != null) {
            routeDefinition.marshal(routeParams.afterTransformDataFormatDefinition());
        }

        if (routeParams.mainProducer() != null) {
            routeDefinition.to(routeParams.mainProducer());
        }

        routeDefinition
                .to(routeParams.successResponseBuilder())
                .log(LOG_BODY);
    }
}
