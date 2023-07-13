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

import static com.bridle.configuration.common.ComponentNameConstants.FREEMARKER_COMPONENT_NAME;
import static com.bridle.configuration.common.ComponentNameConstants.SUCCESS_RESPONSE_FREEMARKER_COMPONENT_NAME;

public class SuccessResponseConfiguration {

    @ConfigurationProperties(prefix = SUCCESS_RESPONSE_FREEMARKER_COMPONENT_NAME)
    @Bean
    public FreemarkerProducerConfiguration successResponseConfiguration() {
        return new FreemarkerProducerConfiguration();
    }

    @Bean(name = SUCCESS_RESPONSE_FREEMARKER_COMPONENT_NAME)
    public FreemarkerComponent freemarkerComponent() {
        return new FreemarkerComponent();
    }

    @Lazy
    @Bean
    public ComponentCustomizer configureSuccessResponseComponent(CamelContext context,
                                                                 @Qualifier("successResponseConfiguration")
                                                                 FreemarkerProducerConfiguration componentConfiguration) {
        return new ComponentCustomizerImpl(context, componentConfiguration, SUCCESS_RESPONSE_FREEMARKER_COMPONENT_NAME);
    }

}
