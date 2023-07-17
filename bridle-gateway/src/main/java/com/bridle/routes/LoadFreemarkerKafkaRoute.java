package com.bridle.routes;

import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;

import static com.bridle.configuration.routes.LoadFreemarkerKafkaConfiguration.LOAD_FREEMARKER_KAFKA;

public class LoadFreemarkerKafkaRoute extends BaseRouteBuilder {

    private final EndpointConsumerBuilder scheduler;

    private final EndpointProducerBuilder freemarker;

    private final EndpointProducerBuilder kafkaOut;


    public LoadFreemarkerKafkaRoute(ErrorHandlerFactory errorHandlerFactory,
                                    EndpointConsumerBuilder scheduler,
                                    EndpointProducerBuilder freemarker,
                                    EndpointProducerBuilder kafkaOut) {
        super(errorHandlerFactory);
        this.scheduler = scheduler;
        this.freemarker = freemarker;
        this.kafkaOut = kafkaOut;
    }

    @Override
    public void configure() throws Exception {
        super.configure();

        from(scheduler)
                .routeId(LOAD_FREEMARKER_KAFKA)
                .to(freemarker)
                .log("Request: ${body}")
                .to(kafkaOut);
    }
}
