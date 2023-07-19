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

import static com.bridle.configuration.common.ComponentNameConstants.REST_POLL_COMPONENT_NAME;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.http;

public class HttpPollConfiguration {

    @ConfigurationProperties(prefix = REST_POLL_COMPONENT_NAME)
    @Bean
    public HttpProducerConfiguration restPollConfiguration() {
        return new HttpProducerConfiguration();
    }

    @Bean(name = REST_POLL_COMPONENT_NAME)
    public HttpComponent restPollComponent() {
        return new HttpComponent();
    }

    @Lazy
    @Bean
    public ComponentCustomizer configureHttpPollComponent(CamelContext context,
                                                          @Qualifier("restPollConfiguration")
                                                          HttpProducerConfiguration componentConfiguration) {
        return new ComponentCustomizerImpl(context, componentConfiguration, REST_POLL_COMPONENT_NAME);
    }

    @Bean
    public EndpointProducerBuilder restPollBuilder(
            @Qualifier("restPollConfiguration") HttpProducerConfiguration configuration) {
        EndpointProducerBuilder result = http(REST_POLL_COMPONENT_NAME, configuration.createHttpUrl());
        configuration.getEndpointProperties().ifPresent(additional -> additional.forEach(result::doSetProperty));
        return result;
    }
}
