package com.bridle.configuration.common.producer;

import com.bridle.properties.HttpProducerConfiguration;
import com.bridle.utils.ComponentCustomizerImpl;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.component.http.HttpComponent;
import org.apache.camel.spi.ComponentCustomizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import static com.bridle.configuration.common.ComponentNameConstants.REST_CALL_COMPONENT_NAME;
import static com.bridle.configuration.common.ComponentNameConstants.REST_POLL_COMPONENT_NAME;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.http;

public class RestCallConfiguration {

    @ConfigurationProperties(prefix = REST_CALL_COMPONENT_NAME)
    @Bean
    public HttpProducerConfiguration restCallConfiguration() {
        return new HttpProducerConfiguration();
    }

    @Bean(name = REST_CALL_COMPONENT_NAME)
    public HttpComponent restCallComponent() {
        return new HttpComponent();
    }

    @Lazy
    @Bean
    public ComponentCustomizer configureHttpComponent(CamelContext context,
                                                      @Qualifier("restCallConfiguration")
                                                      HttpProducerConfiguration componentConfiguration) {
        return new ComponentCustomizerImpl(context, componentConfiguration, REST_CALL_COMPONENT_NAME);
    }

    @Bean
    public EndpointProducerBuilder restCallBuilder(@Qualifier("restCallConfiguration")
                                                   HttpProducerConfiguration configuration) {
        EndpointProducerBuilder result = http(REST_CALL_COMPONENT_NAME, configuration.createHttpUrl());
        configuration.getEndpointProperties().
                ifPresent(additional -> additional.forEach(result::doSetProperty));
        return result;
    }
}
