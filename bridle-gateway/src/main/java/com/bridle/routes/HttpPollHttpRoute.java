package com.bridle.routes;

import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;

import static com.bridle.configuration.routes.HttpPollHttpConfiguration.GATEWAY_TYPE_HTTP_POLL_HTTP;

public class HttpPollHttpRoute extends BaseRouteBuilder {

    private final EndpointConsumerBuilder scheduler;

    private final EndpointProducerBuilder restPoll;

    private final EndpointProducerBuilder restCall;

    public HttpPollHttpRoute(ErrorHandlerFactory errorHandlerFactory,
                             EndpointConsumerBuilder scheduler,
                             EndpointProducerBuilder restPoll,
                             EndpointProducerBuilder restCall) {
        super(errorHandlerFactory);
        this.scheduler = scheduler;
        this.restPoll = restPoll;
        this.restCall = restCall;
    }

    @Override
    public void configure() throws Exception {
        super.configure();

        from(scheduler)
                .routeId(GATEWAY_TYPE_HTTP_POLL_HTTP)
                .to(restPoll)
                .log("Response poll: ${body}")
                .to(restCall)
                .log("Response service ${body}");
    }
}
