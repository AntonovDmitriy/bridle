package com.bridle.configuration;

import com.bridle.configuration.component.HttpProducerConfiguration;
import com.bridle.configuration.component.ValidatedKafkaConsumerConfiguration;
import com.bridle.routes.KafkaHttpRoute;
import com.bridle.utils.ComponentCustomizerImpl;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpComponent;
import org.apache.camel.component.kafka.KafkaComponent;
import org.apache.camel.component.kafka.springboot.KafkaComponentConfiguration;
import org.apache.camel.spi.ComponentCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.http;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.kafka;

@Configuration
@ConditionalOnProperty(name = "gateway.type", havingValue = KafkaHttpConfiguration.GATEWAY_TYPE_KAFKA_HTTP)
public class KafkaHttpConfiguration {

    public static final String GATEWAY_TYPE_KAFKA_HTTP = "kafka-http";

    @ConfigurationProperties(prefix = ComponentNameConstants.KAFKA_IN_COMPONENT_NAME)
    @Bean
    public KafkaComponentConfiguration kafkaInConfiguration() {
        return new ValidatedKafkaConsumerConfiguration();
    }

    @Bean(name = ComponentNameConstants.KAFKA_IN_COMPONENT_NAME)
    public KafkaComponent kafkaInComponent() {
        return new KafkaComponent();
    }

    @Lazy
    @Bean
    public ComponentCustomizer configureKafkaComponent(CamelContext context, KafkaComponentConfiguration componentConfiguration) {
        return new ComponentCustomizerImpl(context, componentConfiguration, ComponentNameConstants.KAFKA_IN_COMPONENT_NAME);
    }

    @ConfigurationProperties(prefix = ComponentNameConstants.REST_CALL_COMPONENT_NAME)
    @Bean
    public HttpProducerConfiguration restCallConfiguration() {
        return new HttpProducerConfiguration();
    }

    @Bean(name = ComponentNameConstants.REST_CALL_COMPONENT_NAME)
    @Lazy
    public HttpComponent restCallComponent() {
        return new HttpComponent();
    }

    @Lazy
    @Bean
    public ComponentCustomizer configureHttpComponent(CamelContext context, HttpProducerConfiguration componentConfiguration) {
        return new ComponentCustomizerImpl(context, componentConfiguration, ComponentNameConstants.REST_CALL_COMPONENT_NAME);
    }

    @Bean
    public RouteBuilder kafkaHttpRoute(ValidatedKafkaConsumerConfiguration kafkaConfiguration,
                                       HttpProducerConfiguration restConfiguration) {

        EndpointConsumerBuilder kafka = kafka(ComponentNameConstants.KAFKA_IN_COMPONENT_NAME, kafkaConfiguration.getTopic());
        kafkaConfiguration.getEndpointProperties().ifPresent(additional -> additional.forEach(kafka::doSetProperty));

        EndpointProducerBuilder http = http(ComponentNameConstants.REST_CALL_COMPONENT_NAME, restConfiguration.createHttpUrl());
        restConfiguration.getEndpointProperties().ifPresent(additional -> additional.forEach(http::doSetProperty));

        return new KafkaHttpRoute(kafka, http);
    }
}
