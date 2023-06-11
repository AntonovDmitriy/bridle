package com.bridle.routes;

import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;

import static com.bridle.configuration.HttpPollHttpConfiguration.GATEWAY_TYPE_HTTP_POLL_HTTP;

public class HttpPollHttpRoute extends RouteBuilder {


    private final EndpointConsumerBuilder scheduler;
    private final EndpointProducerBuilder restPoll;
    private final EndpointProducerBuilder restCall;

    public HttpPollHttpRoute(EndpointConsumerBuilder scheduler, EndpointProducerBuilder restPoll, EndpointProducerBuilder restCall) {
        this.scheduler = scheduler;
        this.restPoll = restPoll;
        this.restCall = restCall;
    }

    @Override
    public void configure() throws Exception {
        from(scheduler)
                .routeId(GATEWAY_TYPE_HTTP_POLL_HTTP)
                .to(restPoll)
                .log("Response poll: ${body}")
                .to(restCall)
                .log("Response service ${body}");
    }
}
