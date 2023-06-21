package com.bridle.routes;

import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;

import static com.bridle.configuration.KafkaHttpConfiguration.GATEWAY_TYPE_KAFKA_HTTP;

public class KafkaHttpRoute extends RouteBuilder {

    private final EndpointConsumerBuilder kafkaIn;

    private final EndpointProducerBuilder restCall;

    public KafkaHttpRoute(EndpointConsumerBuilder kafkaIn, EndpointProducerBuilder restCall) {
        this.kafkaIn = kafkaIn;
        this.restCall = restCall;
    }

    @Override
    public void configure() throws Exception {
        from(kafkaIn)
                .routeId(GATEWAY_TYPE_KAFKA_HTTP)
                .log("Request: ${body}")
                .to(restCall)
                .log("Response ${body}");
    }
}
