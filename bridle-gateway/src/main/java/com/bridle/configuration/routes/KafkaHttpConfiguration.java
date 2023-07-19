package com.bridle.configuration.routes;

import com.bridle.configuration.common.ComponentNameConstants;
import com.bridle.configuration.common.consumer.KafkaInConfiguration;
import com.bridle.configuration.common.errorhandling.ErrorHandlerConfiguration;
import com.bridle.configuration.common.producer.RestCallConfiguration;
import com.bridle.properties.HttpProducerConfiguration;
import com.bridle.properties.ValidatedKafkaConsumerConfiguration;
import com.bridle.routes.KafkaHttpRoute;
import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static com.bridle.configuration.common.ComponentNameConstants.REST_CALL_COMPONENT_NAME;
import static com.bridle.configuration.routes.KafkaHttpConfiguration.GATEWAY_TYPE_KAFKA_HTTP;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.http;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.kafka;

@Configuration @Import({KafkaInConfiguration.class, RestCallConfiguration.class, ErrorHandlerConfiguration.class})
@ConditionalOnProperty(name = "gateway.type", havingValue = GATEWAY_TYPE_KAFKA_HTTP)
public class KafkaHttpConfiguration {

    public static final String GATEWAY_TYPE_KAFKA_HTTP = "kafka-http";

    @Bean
    public RouteBuilder kafkaHttpRoute(ErrorHandlerFactory errorHandlerFactory,
                                       ValidatedKafkaConsumerConfiguration kafkaConfiguration,
                                       HttpProducerConfiguration restConfiguration) {

        EndpointConsumerBuilder kafka =
                kafka(ComponentNameConstants.KAFKA_IN_COMPONENT_NAME, kafkaConfiguration.getTopic());
        kafkaConfiguration.getEndpointProperties().ifPresent(additional -> additional.forEach(kafka::doSetProperty));

        EndpointProducerBuilder http = http(REST_CALL_COMPONENT_NAME, restConfiguration.createHttpUrl());
        restConfiguration.getEndpointProperties().ifPresent(additional -> additional.forEach(http::doSetProperty));

        return new KafkaHttpRoute(errorHandlerFactory, kafka, http);
    }
}
