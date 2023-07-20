package com.bridle.routes;

import com.bridle.utils.BaseRouteBuilder;
import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;

import static com.bridle.configuration.routes.KafkaHttpKafkaConfiguration.GATEWAY_TYPE_KAFKA_HTTP_KAFKA;

public class KafkaHttpKafkaRoute extends BaseRouteBuilder {


    private final EndpointConsumerBuilder kafkaIn;

    private final EndpointProducerBuilder restCall;

    private final EndpointProducerBuilder kafkaOut;


    public KafkaHttpKafkaRoute(ErrorHandlerFactory errorHandlerFactory,
            EndpointConsumerBuilder kafkaIn,
            EndpointProducerBuilder restCall,
            EndpointProducerBuilder kafkaOut) {
        super(errorHandlerFactory);
        this.kafkaIn = kafkaIn;
        this.restCall = restCall;
        this.kafkaOut = kafkaOut;
    }

    @Override
    public void configure() throws Exception {
        super.configure();

        from(kafkaIn)
                .routeId(GATEWAY_TYPE_KAFKA_HTTP_KAFKA)
                .log("Request: ${body}")
                .to(restCall)
                .log("Response ${body}")
                .to(kafkaOut);


    }
}
