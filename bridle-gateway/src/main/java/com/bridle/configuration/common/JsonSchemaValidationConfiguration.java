package com.bridle.configuration.common;

import com.bridle.properties.JsonSchemaValidatorConfiguration;
import com.bridle.utils.ComponentCustomizerImpl;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.component.jsonvalidator.JsonValidatorComponent;
import org.apache.camel.spi.ComponentCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import static com.bridle.configuration.common.ComponentNameConstants.JSON_VALIDATOR_COMPONENT_NAME;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.jsonValidator;


public class JsonSchemaValidationConfiguration {

    @ConditionalOnProperty(value = "inbound-validation.format", havingValue = "json-schema")
    @ConfigurationProperties(prefix = "inbound-validation." + JSON_VALIDATOR_COMPONENT_NAME)
    @Bean
    public JsonSchemaValidatorConfiguration jsonValidatorConfiguration() {
        return new JsonSchemaValidatorConfiguration();
    }

    @Bean(name = JSON_VALIDATOR_COMPONENT_NAME)
    @ConditionalOnBean(name = "jsonValidatorConfiguration")
    public JsonValidatorComponent jsonValidatorComponent() {
        return new JsonValidatorComponent();
    }

    @Bean
    @ConditionalOnBean(name = "jsonValidatorConfiguration")
    public EndpointProducerBuilder validatorBuilder(JsonSchemaValidatorConfiguration validatorConfiguration) {
        EndpointProducerBuilder result = jsonValidator(JSON_VALIDATOR_COMPONENT_NAME, validatorConfiguration.getResourceUri());
        validatorConfiguration.getEndpointProperties()
                .ifPresent(additional -> additional.forEach(result::doSetProperty));
        return result;
    }

    @Lazy
    @Bean
    @ConditionalOnBean(name = "jsonValidatorConfiguration")
    public ComponentCustomizer configureJsonValidatorComponent(CamelContext context,
                                                               JsonSchemaValidatorConfiguration componentConfiguration) {
        return new ComponentCustomizerImpl(context, componentConfiguration, JSON_VALIDATOR_COMPONENT_NAME);
    }

}
