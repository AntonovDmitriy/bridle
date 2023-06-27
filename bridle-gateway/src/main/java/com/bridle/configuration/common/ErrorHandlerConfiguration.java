package com.bridle.configuration.common;

import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.model.errorhandler.DefaultErrorHandlerDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import static com.bridle.configuration.common.ComponentNameConstants.ERROR_HANDLER_NAME;

public class ErrorHandlerConfiguration {

    @ConfigurationProperties(prefix = ERROR_HANDLER_NAME)
    @Bean
    public ErrorHandlerFactory errorHandlerFactory() {
        return new DefaultErrorHandlerDefinition();
    }
}
