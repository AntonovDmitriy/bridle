package com.bridle.routes;

import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;

import static com.bridle.configuration.HttpPollKafkaConfiguration.GATEWAY_TYPE_HTTP_POLL_KAFKA;

public class HttpPollKafkaRoute extends RouteBuilder {

    private final EndpointConsumerBuilder scheduler;

    private final EndpointProducerBuilder restPoll;

    private final EndpointProducerBuilder kafka;

    public HttpPollKafkaRoute(EndpointConsumerBuilder scheduler,
                              EndpointProducerBuilder restPoll,
                              EndpointProducerBuilder kafka) {
        this.scheduler = scheduler;
        this.restPoll = restPoll;
        this.kafka = kafka;
    }

    @Override
    public void configure() throws Exception {
        from(scheduler)
                .routeId(GATEWAY_TYPE_HTTP_POLL_KAFKA)
                .to(restPoll)
                .log("Response poll: ${body}")
                .to(kafka);
    }
}
