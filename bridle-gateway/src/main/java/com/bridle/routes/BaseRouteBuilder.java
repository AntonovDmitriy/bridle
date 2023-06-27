package com.bridle.routes;

import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.builder.RouteBuilder;

public class BaseRouteBuilder extends RouteBuilder {


    private final ErrorHandlerFactory errorHandlerFactory;

    public BaseRouteBuilder(ErrorHandlerFactory errorHandlerFactory) {
        this.errorHandlerFactory = errorHandlerFactory;
    }

    @Override
    public void configure() throws Exception {
        errorHandler(errorHandlerFactory);
    }
}
