package com.bridle.configuration;

import com.bridle.properties.ValidatedKafkaProducerConfiguration;
import com.bridle.utils.ComponentCustomizerImpl;
import org.apache.camel.CamelContext;
import org.apache.camel.component.kafka.KafkaComponent;
import org.apache.camel.spi.ComponentCustomizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import static com.bridle.configuration.ComponentNameConstants.KAFKA_OUT_COMPONENT_NAME;

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

    @Lazy
    @Bean
    public ComponentCustomizer configureKafkaOutComponent(CamelContext context,
                                                          ValidatedKafkaProducerConfiguration componentConfiguration) {
        return new ComponentCustomizerImpl(context, componentConfiguration, KAFKA_OUT_COMPONENT_NAME);
    }
}
