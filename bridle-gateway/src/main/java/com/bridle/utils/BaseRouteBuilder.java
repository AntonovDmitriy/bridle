package com.bridle.utils;

import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.builder.RouteBuilder;

public class BaseRouteBuilder extends RouteBuilder {

    public static final String LOG_BODY = "Response: ${body}";

    private final ErrorHandlerFactory errorHandlerFactory;

    public BaseRouteBuilder(ErrorHandlerFactory errorHandlerFactory) {
        this.errorHandlerFactory = errorHandlerFactory;
    }

    @Override
    public void configure() throws Exception {
        errorHandler(errorHandlerFactory);
    }
}
