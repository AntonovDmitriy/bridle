package com.bridle.configuration.common.errorhandling;

import com.bridle.properties.FreemarkerProducerConfiguration;
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

import static com.bridle.configuration.common.ComponentNameConstants.VALIDATION_RESPONSE_FREEMARKER_COMPONENT_NAME;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.freemarker;

public class ValidationErrorResponseConfiguration {

    @ConfigurationProperties(prefix = VALIDATION_RESPONSE_FREEMARKER_COMPONENT_NAME)
    @Bean
    public FreemarkerProducerConfiguration validationErrorResponseConfiguration() {
        FreemarkerProducerConfiguration configuration = new FreemarkerProducerConfiguration();
        if (StringUtils.isBlank(configuration.getResourceUri())) {
            configuration.setResourceUri("classpath:http-kafka/validation-error-response.tmpl");
        }
        return configuration;
    }

    @Bean(name = VALIDATION_RESPONSE_FREEMARKER_COMPONENT_NAME)
    public FreemarkerComponent freemarkerComponent() {
        return new FreemarkerComponent();
    }

    @Bean
    public EndpointProducerBuilder validationErrorResponseBuilder(@Qualifier("validationErrorResponseConfiguration")
    FreemarkerProducerConfiguration errorResponseConfiguration) {
        EndpointProducerBuilder result =
                freemarker(VALIDATION_RESPONSE_FREEMARKER_COMPONENT_NAME, errorResponseConfiguration.getResourceUri());
        errorResponseConfiguration.getEndpointProperties()
                .ifPresent(additional -> additional.forEach(result::doSetProperty));
        return result;
    }

    @Lazy
    @Bean
    public ComponentCustomizer configureValidationErrorResponseComponent(CamelContext context,
            @Qualifier("validationErrorResponseConfiguration") FreemarkerProducerConfiguration componentConfiguration) {
        return new ComponentCustomizerImpl(context,
                                           componentConfiguration,
                                           VALIDATION_RESPONSE_FREEMARKER_COMPONENT_NAME);
    }

}
