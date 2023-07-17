package com.bridle.configuration.common;

import com.bridle.properties.FreemarkerProducerConfiguration;
import com.bridle.properties.ValidatedKafkaProducerConfiguration;
import com.bridle.utils.ComponentCustomizerImpl;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.component.freemarker.FreemarkerComponent;
import org.apache.camel.spi.ComponentCustomizer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import static com.bridle.configuration.common.ComponentNameConstants.FREEMARKER_COMPONENT_NAME;
import static com.bridle.configuration.common.ComponentNameConstants.KAFKA_OUT_COMPONENT_NAME;
import static com.bridle.configuration.common.ComponentNameConstants.SUCCESS_RESPONSE_FREEMARKER_COMPONENT_NAME;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.freemarker;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.kafka;

public class SuccessResponseConfiguration {

    @ConfigurationProperties(prefix = SUCCESS_RESPONSE_FREEMARKER_COMPONENT_NAME)
    @Bean
    public FreemarkerProducerConfiguration successResponseConfiguration() {
        FreemarkerProducerConfiguration configuration = new FreemarkerProducerConfiguration();
        if (StringUtils.isBlank(configuration.getResourceUri())) {
            configuration.setResourceUri("classpath:http-kafka/success-response.tmpl");
        }
        return configuration;
    }

    @Bean(name = SUCCESS_RESPONSE_FREEMARKER_COMPONENT_NAME)
    public FreemarkerComponent freemarkerComponent() {
        return new FreemarkerComponent();
    }

    @Bean
    public EndpointProducerBuilder successResponseBuilder(@Qualifier("successResponseConfiguration")
                                                          FreemarkerProducerConfiguration successResponseConfiguration) {
        EndpointProducerBuilder result = freemarker(SUCCESS_RESPONSE_FREEMARKER_COMPONENT_NAME,
                successResponseConfiguration.getResourceUri());
        successResponseConfiguration.getEndpointProperties()
                .ifPresent(additional -> additional.forEach(result::doSetProperty));
        return result;
    }

    @Lazy
    @Bean
    public ComponentCustomizer configureSuccessResponseComponent(CamelContext context,
                                                                 @Qualifier("successResponseConfiguration")
                                                                 FreemarkerProducerConfiguration componentConfiguration) {
        return new ComponentCustomizerImpl(context, componentConfiguration, SUCCESS_RESPONSE_FREEMARKER_COMPONENT_NAME);
    }

}
