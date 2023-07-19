package com.bridle.routes;

import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;

import static com.bridle.configuration.routes.KafkaHttpConfiguration.GATEWAY_TYPE_KAFKA_HTTP;

public class KafkaHttpRoute extends BaseRouteBuilder {

    private final EndpointConsumerBuilder kafkaIn;

    private final EndpointProducerBuilder restCall;

    public KafkaHttpRoute(ErrorHandlerFactory errorHandlerFactory,
                          EndpointConsumerBuilder kafkaIn,
                          EndpointProducerBuilder restCall) {
        super(errorHandlerFactory);
        this.kafkaIn = kafkaIn;
        this.restCall = restCall;
    }

    @Override
    public void configure() throws Exception {
        super.configure();

        from(kafkaIn).routeId(GATEWAY_TYPE_KAFKA_HTTP).log("Request: ${body}").to(restCall).log("Response ${body}");
    }
}
