package com.bridle.configuration.common.errorhandling;

import org.apache.camel.CamelContext;
import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.model.RedeliveryPolicyDefinition;
import org.apache.camel.model.errorhandler.DefaultErrorHandlerDefinition;
import org.apache.camel.processor.errorhandler.RedeliveryPolicy;
import org.apache.camel.reifier.errorhandler.ErrorHandlerReifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import static com.bridle.configuration.common.ComponentNameConstants.ERROR_HANDLER_NAME;
import static com.bridle.configuration.common.ComponentNameConstants.REDELIVERY_POLICY;

public class ErrorHandlerConfiguration {

    @ConfigurationProperties(prefix = REDELIVERY_POLICY)
    @Bean
    public RedeliveryPolicyDefinition customRedeliveryPolicyDefinition() {
        return new RedeliveryPolicyDefinition();
    }

    @Bean(name = REDELIVERY_POLICY)
    public RedeliveryPolicy customRedeliveryPolicy(RedeliveryPolicyDefinition myRedeliveryPolicyDefinition, CamelContext context) {
        return ErrorHandlerReifier.createRedeliveryPolicy(myRedeliveryPolicyDefinition, context);
    }

    @ConfigurationProperties(prefix = ERROR_HANDLER_NAME)
    @Bean
    public ErrorHandlerFactory errorHandlerFactory() {
        return new DefaultErrorHandlerDefinition();
    }
}
