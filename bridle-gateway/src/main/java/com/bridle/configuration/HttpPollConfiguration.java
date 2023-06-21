package com.bridle.configuration;

import com.bridle.properties.HttpProducerConfiguration;
import com.bridle.utils.ComponentCustomizerImpl;
import org.apache.camel.CamelContext;
import org.apache.camel.component.http.HttpComponent;
import org.apache.camel.spi.ComponentCustomizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import static com.bridle.configuration.ComponentNameConstants.REST_POLL_COMPONENT_NAME;

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
}
