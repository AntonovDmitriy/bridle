package com.bridle.routes;

import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;

import static com.bridle.configuration.KafkaHttpKafkaConfiguration.GATEWAY_TYPE_KAFKA_HTTP_KAFKA;

public class KafkaHttpKafkaRoute extends RouteBuilder {


    private final EndpointConsumerBuilder kafkaIn;
    private final EndpointProducerBuilder restCall;
    private final EndpointProducerBuilder kafkaOut;


    public KafkaHttpKafkaRoute(EndpointConsumerBuilder kafkaIn,
                               EndpointProducerBuilder restCall,
                               EndpointProducerBuilder kafkaOut) {
        this.kafkaIn = kafkaIn;
        this.restCall = restCall;
        this.kafkaOut = kafkaOut;
    }

    @Override
    public void configure() throws Exception {
        from(kafkaIn)
                .routeId(GATEWAY_TYPE_KAFKA_HTTP_KAFKA)
                .log("Request: ${body}")
                .to(restCall)
                .log("Response ${body}")
                .to(kafkaOut);
    }
}
