package com.bridle.utils;

import com.bridle.properties.HttpConsumerConfiguration;
import org.apache.camel.ErrorHandlerFactory;

public abstract class AbstractHttpConsumerRoute extends BaseRouteBuilder {

    protected final HttpConsumerConfiguration restConfiguration;

    protected AbstractHttpConsumerRoute(ErrorHandlerFactory errorHandlerFactory,
            HttpConsumerConfiguration restConfiguration) {
        super(errorHandlerFactory);
        this.restConfiguration = restConfiguration;
    }

    @Override
    public void configure() throws Exception {
        super.configure();
        getContext().setRestConfiguration(restConfiguration);
        rest().getVerbs().addAll(restConfiguration.getRoutes());
    }
}
