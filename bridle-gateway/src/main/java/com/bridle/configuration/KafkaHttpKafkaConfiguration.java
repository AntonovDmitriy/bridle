package com.bridle.configuration;

import com.bridle.properties.HttpProducerConfiguration;
import com.bridle.properties.ValidatedKafkaConsumerConfiguration;
import com.bridle.properties.ValidatedKafkaProducerConfiguration;
import com.bridle.routes.KafkaHttpKafkaRoute;
import io.micrometer.core.instrument.Clock;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.CollectorRegistry;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.micrometer.MicrometerConstants;
import org.apache.camel.component.micrometer.routepolicy.MicrometerRoutePolicyFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static com.bridle.configuration.ComponentNameConstants.KAFKA_IN_COMPONENT_NAME;
import static com.bridle.configuration.ComponentNameConstants.KAFKA_OUT_COMPONENT_NAME;
import static com.bridle.configuration.ComponentNameConstants.REST_CALL_COMPONENT_NAME;
import static com.bridle.configuration.KafkaHttpKafkaConfiguration.GATEWAY_TYPE_KAFKA_HTTP_KAFKA;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.http;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.kafka;

@Configuration
@Import({KafkaInConfiguration.class, RestCallConfiguration.class, KafkaOutConfiguration.class})
@ConditionalOnProperty(name = "gateway.type", havingValue = GATEWAY_TYPE_KAFKA_HTTP_KAFKA)
public class KafkaHttpKafkaConfiguration {

    public static final String GATEWAY_TYPE_KAFKA_HTTP_KAFKA = "kafka-http-kafka";

    @Bean
    public RouteBuilder kafkaHttpKafkaRoute(ValidatedKafkaConsumerConfiguration kafkaInConfiguration,
                                            HttpProducerConfiguration restConfiguration,
                                            ValidatedKafkaProducerConfiguration kafkaOutConfiguration) {

        EndpointConsumerBuilder kafkaIn = kafka(KAFKA_IN_COMPONENT_NAME, kafkaInConfiguration.getTopic());
        kafkaInConfiguration.getEndpointProperties().
                ifPresent(additional -> additional.forEach(kafkaIn::doSetProperty));

        EndpointProducerBuilder http = http(REST_CALL_COMPONENT_NAME, restConfiguration.createHttpUrl());
        restConfiguration.getEndpointProperties().ifPresent(additional -> additional.forEach(http::doSetProperty));

        EndpointProducerBuilder kafkaOut = kafka(KAFKA_OUT_COMPONENT_NAME, kafkaOutConfiguration.getTopic());
        kafkaOutConfiguration.getEndpointProperties()
                .ifPresent(additional -> additional.forEach(kafkaOut::doSetProperty));

        return new KafkaHttpKafkaRoute(kafkaIn, http, kafkaOut);
    }
}
