package com.bridle.routes;

import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;

import static com.bridle.configuration.routes.LoadFreemarkerHttpConfiguration.LOAD_FREEMARKER_HTTP;

public class LoadFreemarkerHttpRoute extends BaseRouteBuilder {

    private final EndpointConsumerBuilder scheduler;

    private final EndpointProducerBuilder freemarker;

    private final EndpointProducerBuilder restCall;


    public LoadFreemarkerHttpRoute(ErrorHandlerFactory errorHandlerFactory,
                                   EndpointConsumerBuilder scheduler,
                                   EndpointProducerBuilder freemarker,
                                   EndpointProducerBuilder restCall){
        super(errorHandlerFactory);
        this.scheduler = scheduler;
        this.freemarker = freemarker;
        this.restCall = restCall;
    }

    @Override
    public void configure() throws Exception {
        super.configure();

        from(scheduler)
                .routeId(LOAD_FREEMARKER_HTTP)
                .to(freemarker)
                .log("Request: ${body}")
                .to(restCall)
                .log("Response ${body}");
    }
}
