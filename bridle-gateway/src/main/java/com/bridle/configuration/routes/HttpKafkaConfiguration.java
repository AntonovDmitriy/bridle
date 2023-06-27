package com.bridle.configuration.routes;

import com.bridle.configuration.common.ErrorHandlerConfiguration;
import com.bridle.configuration.common.KafkaOutConfiguration;
import com.bridle.properties.HttpConsumerConfiguration;
import com.bridle.properties.ValidatedKafkaProducerConfiguration;
import com.bridle.routes.HttpKafkaRoute;
import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static com.bridle.configuration.common.ComponentNameConstants.KAFKA_OUT_COMPONENT_NAME;
import static com.bridle.configuration.common.ComponentNameConstants.REST_IN_COMPONENT_NAME;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.kafka;

@Configuration
@Import({KafkaOutConfiguration.class, ErrorHandlerConfiguration.class})
@ConditionalOnProperty(name = "gateway.type", havingValue = HttpKafkaConfiguration.GATEWAY_TYPE_HTTP_KAFKA)
public class HttpKafkaConfiguration {

    public static final String GATEWAY_TYPE_HTTP_KAFKA = "http-kafka";

    @ConfigurationProperties(prefix = REST_IN_COMPONENT_NAME)
    @Bean
    public HttpConsumerConfiguration restInConfiguration() {
        return new HttpConsumerConfiguration();
    }

    @Bean
    public RouteBuilder kafkaHttpKafkaRoute(ErrorHandlerFactory errorHandlerFactory,
                                            ValidatedKafkaProducerConfiguration kafkaOutConfiguration,
                                            HttpConsumerConfiguration httpConsumerConfiguration) {

        EndpointProducerBuilder kafkaOut = kafka(KAFKA_OUT_COMPONENT_NAME, kafkaOutConfiguration.getTopic());
        kafkaOutConfiguration.getEndpointProperties()
                .ifPresent(additional -> additional.forEach(kafkaOut::doSetProperty));

        return new HttpKafkaRoute(errorHandlerFactory, httpConsumerConfiguration, kafkaOut);
    }
}
