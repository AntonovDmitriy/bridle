package com.bridle.routes;

import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;

import static com.bridle.configuration.LoadFreemarkerKafkaConfiguration.LOAD_FREEMARKER_KAFKA;

public class LoadFreemarkerKafkaRoute extends RouteBuilder {

    private final EndpointConsumerBuilder scheduler;

    private final EndpointProducerBuilder freemarker;

    private final EndpointProducerBuilder kafkaOut;


    public LoadFreemarkerKafkaRoute(EndpointConsumerBuilder scheduler,
                                    EndpointProducerBuilder freemarker,
                                    EndpointProducerBuilder kafkaOut){
        this.scheduler = scheduler;
        this.freemarker = freemarker;
        this.kafkaOut = kafkaOut;
    }

    @Override
    public void configure() throws Exception {
        from(scheduler)
                .routeId(LOAD_FREEMARKER_KAFKA)
                .to(freemarker)
                .log("Request: ${body}")
                .to(kafkaOut);
    }
}
