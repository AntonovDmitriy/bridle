package com.bridle.routes;

import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;

import static com.bridle.configuration.LoadFreemarkerHttpConfiguration.LOAD_FREEMARKER_HTTP;

public class LoadFreemarkerHttpRoute extends RouteBuilder {

    private final EndpointConsumerBuilder scheduler;

    private final EndpointProducerBuilder freemarker;

    private final EndpointProducerBuilder restCall;


    public LoadFreemarkerHttpRoute(EndpointConsumerBuilder scheduler,
                                   EndpointProducerBuilder freemarker,
                                   EndpointProducerBuilder restCall){
        this.scheduler = scheduler;
        this.freemarker = freemarker;
        this.restCall = restCall;
    }

    @Override
    public void configure() throws Exception {
        from(scheduler)
                .routeId(LOAD_FREEMARKER_HTTP)
                .to(freemarker)
                .log("Request: ${body}")
                .to(restCall)
                .log("Response ${body}");
    }
}
