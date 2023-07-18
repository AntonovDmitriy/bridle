package com.bridle.configuration.routes;

import com.bridle.configuration.common.ErrorHandlerConfiguration;
import com.bridle.configuration.common.KafkaInConfiguration;
import com.bridle.configuration.common.KafkaOutConfiguration;
import com.bridle.configuration.common.RestCallConfiguration;
import com.bridle.properties.HttpProducerConfiguration;
import com.bridle.properties.ValidatedKafkaConsumerConfiguration;
import com.bridle.properties.ValidatedKafkaProducerConfiguration;
import com.bridle.routes.KafkaHttpKafkaRoute;
import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static com.bridle.configuration.common.ComponentNameConstants.KAFKA_IN_COMPONENT_NAME;
import static com.bridle.configuration.common.ComponentNameConstants.KAFKA_OUT_COMPONENT_NAME;
import static com.bridle.configuration.common.ComponentNameConstants.REST_CALL_COMPONENT_NAME;
import static com.bridle.configuration.routes.KafkaHttpKafkaConfiguration.GATEWAY_TYPE_KAFKA_HTTP_KAFKA;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.http;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.kafka;

@Configuration
@Import({KafkaInConfiguration.class,
        RestCallConfiguration.class,
        KafkaOutConfiguration.class,
        ErrorHandlerConfiguration.class})
@ConditionalOnProperty(name = "gateway.type", havingValue = GATEWAY_TYPE_KAFKA_HTTP_KAFKA)
public class KafkaHttpKafkaConfiguration {

    public static final String GATEWAY_TYPE_KAFKA_HTTP_KAFKA = "kafka-http-kafka";

    @Bean
    public RouteBuilder kafkaHttpKafkaRoute(ErrorHandlerFactory errorHandlerFactory,
                                            ValidatedKafkaConsumerConfiguration kafkaInConfiguration,
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

        return new KafkaHttpKafkaRoute(errorHandlerFactory, kafkaIn, http, kafkaOut);
    }
}
