package com.bridle.configuration.common;

import com.bridle.properties.FreemarkerProducerConfiguration;
import com.bridle.utils.ComponentCustomizerImpl;
import org.apache.camel.CamelContext;
import org.apache.camel.component.freemarker.FreemarkerComponent;
import org.apache.camel.spi.ComponentCustomizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import static com.bridle.configuration.common.ComponentNameConstants.ERROR_RESPONSE_FREEMARKER_COMPONENT_NAME;
import static com.bridle.configuration.common.ComponentNameConstants.FREEMARKER_COMPONENT_NAME;

public class ErrorResponseConfiguration {

    @ConfigurationProperties(prefix = ERROR_RESPONSE_FREEMARKER_COMPONENT_NAME)
    @Bean
    public FreemarkerProducerConfiguration errorResponseConfiguration() {
        return new FreemarkerProducerConfiguration();
    }

    @Bean(name = ERROR_RESPONSE_FREEMARKER_COMPONENT_NAME)
    public FreemarkerComponent freemarkerComponent() {
        return new FreemarkerComponent();
    }

    @Lazy
    @Bean
    public ComponentCustomizer configureErrorResponseComponent(CamelContext context,
                                                               @Qualifier("errorResponseConfiguration")
                                                               FreemarkerProducerConfiguration componentConfiguration) {
        return new ComponentCustomizerImpl(context, componentConfiguration, ERROR_RESPONSE_FREEMARKER_COMPONENT_NAME);
    }

}
