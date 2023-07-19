package com.bridle.routes;

import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;

import static com.bridle.configuration.routes.HttpPollKafkaConfiguration.GATEWAY_TYPE_HTTP_POLL_KAFKA;

public class HttpPollKafkaRoute extends BaseRouteBuilder {

    private final EndpointConsumerBuilder scheduler;

    private final EndpointProducerBuilder restPoll;

    private final EndpointProducerBuilder kafka;

    public HttpPollKafkaRoute(ErrorHandlerFactory errorHandlerFactory,
                              EndpointConsumerBuilder scheduler,
                              EndpointProducerBuilder restPoll,
                              EndpointProducerBuilder kafka) {
        super(errorHandlerFactory);
        this.scheduler = scheduler;
        this.restPoll = restPoll;
        this.kafka = kafka;
    }

    @Override
    public void configure() throws Exception {
        super.configure();

        from(scheduler).routeId(GATEWAY_TYPE_HTTP_POLL_KAFKA).to(restPoll).log("Response poll: ${body}").to(kafka);
    }
}
