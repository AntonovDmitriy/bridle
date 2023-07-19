package com.bridle.configuration.common.producer;

import com.bridle.properties.ValidatedKafkaProducerConfiguration;
import com.bridle.utils.ComponentCustomizerImpl;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.apache.camel.spi.ComponentCustomizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import static com.bridle.configuration.common.ComponentNameConstants.KAFKA_OUT_COMPONENT_NAME;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.kafka;

public class KafkaOutConfiguration {

    @ConfigurationProperties(prefix = KAFKA_OUT_COMPONENT_NAME)
    @Bean
    public ValidatedKafkaProducerConfiguration kafkaOutConfiguration() {
        return new ValidatedKafkaProducerConfiguration();
    }

    @Bean(name = KAFKA_OUT_COMPONENT_NAME)
    public KafkaComponent kafkaOutComponent() {
        return new KafkaComponent();
    }

    @Bean
    public EndpointProducerBuilder kafkaProducerBuilder(ValidatedKafkaProducerConfiguration kafkaOutConfiguration) {
        EndpointProducerBuilder result = kafka(KAFKA_OUT_COMPONENT_NAME, kafkaOutConfiguration.getTopic());
        kafkaOutConfiguration
                .getEndpointProperties()
                .ifPresent(additional -> additional.forEach(result::doSetProperty));
        return result;
    }

    @Lazy
    @Bean
    public ComponentCustomizer configureKafkaOutComponent(CamelContext context,
            ValidatedKafkaProducerConfiguration componentConfiguration) {
        return new ComponentCustomizerImpl(context, componentConfiguration, KAFKA_OUT_COMPONENT_NAME);
    }

    @Bean
    public EndpointProducerBuilder kafkaOutBuilder(
            @Qualifier("kafkaOutConfiguration") ValidatedKafkaProducerConfiguration configuration) {

        EndpointProducerBuilder result = kafka(KAFKA_OUT_COMPONENT_NAME, configuration.getTopic());
        configuration
                .getEndpointProperties()
                .ifPresent(additional -> additional.forEach(result::doSetProperty));
        return result;
    }
}
