package com.bridle.routes;

import com.bridle.properties.HttpConsumerConfiguration;
import org.apache.camel.builder.RouteBuilder;

import java.util.List;

public class GenericHttpConsumerRoute  extends RouteBuilder {

    private final HttpConsumerConfiguration restConfiguration;

    public GenericHttpConsumerRoute(HttpConsumerConfiguration restConfiguration) {
        this.restConfiguration = restConfiguration;
    }

    @Override
    public void configure() throws Exception {
        getContext().setRestConfiguration(restConfiguration);
        rest().getVerbs().addAll(restConfiguration.getRoutes());
    }
}
