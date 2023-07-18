package com.bridle.configuration.common;

import com.bridle.properties.ValidatedKafkaConsumerConfiguration;
import com.bridle.utils.ComponentCustomizerImpl;
import org.apache.camel.CamelContext;
import org.apache.camel.component.kafka.KafkaComponent;
import org.apache.camel.spi.ComponentCustomizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

public class KafkaInConfiguration {

    @ConfigurationProperties(prefix = ComponentNameConstants.KAFKA_IN_COMPONENT_NAME)
    @Bean
    public ValidatedKafkaConsumerConfiguration kafkaInConfiguration() {
        return new ValidatedKafkaConsumerConfiguration();
    }

    @Bean(name = ComponentNameConstants.KAFKA_IN_COMPONENT_NAME)
    public KafkaComponent kafkaInComponent() {
        return new KafkaComponent();
    }

    @Lazy
    @Bean
    public ComponentCustomizer configureKafkaComponent(CamelContext context,
                                                       ValidatedKafkaConsumerConfiguration componentConfiguration) {
        return new ComponentCustomizerImpl(context, componentConfiguration,
                ComponentNameConstants.KAFKA_IN_COMPONENT_NAME);
    }
}
