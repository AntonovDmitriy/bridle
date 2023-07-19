package com.bridle.routes;

import com.bridle.properties.HttpConsumerConfiguration;
import org.apache.camel.ErrorHandlerFactory;

public class GenericHttpConsumerRoute extends BaseRouteBuilder {

    protected final HttpConsumerConfiguration restConfiguration;

    public GenericHttpConsumerRoute(ErrorHandlerFactory errorHandlerFactory,
            HttpConsumerConfiguration restConfiguration) {
        super(errorHandlerFactory);
        this.restConfiguration = restConfiguration;
    }

    @Override
    public void configure() throws Exception {
        super.configure();
        getContext().setRestConfiguration(restConfiguration);
        rest()
                .getVerbs()
                .addAll(restConfiguration.getRoutes());
    }
}
