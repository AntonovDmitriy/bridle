package com.bridle.configuration;

import com.bridle.properties.HttpConsumerConfiguration;
import com.bridle.properties.ValidatedKafkaProducerConfiguration;
import com.bridle.routes.HttpKafkaRoute;
import com.bridle.utils.ComponentCustomizerImpl;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.apache.camel.component.rest.RestComponent;
import org.apache.camel.spi.ComponentCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import static com.bridle.configuration.ComponentNameConstants.KAFKA_OUT_COMPONENT_NAME;
import static com.bridle.configuration.ComponentNameConstants.REST_IN_COMPONENT_NAME;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.*;

@Configuration
@ConditionalOnProperty(name = "gateway.type", havingValue = HttpKafkaConfiguration.GATEWAY_TYPE_HTTP_KAFKA)
public class HttpKafkaConfiguration {

    public static final String GATEWAY_TYPE_HTTP_KAFKA = "http-kafka";


    @ConfigurationProperties(prefix = REST_IN_COMPONENT_NAME)
    @Bean
    public HttpConsumerConfiguration restInConfiguration(){
        return new HttpConsumerConfiguration();
    }

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
    public ComponentCustomizer configureKafkaOutComponent(CamelContext context,  ValidatedKafkaProducerConfiguration componentConfiguration) {
        return new ComponentCustomizerImpl(context, componentConfiguration, KAFKA_OUT_COMPONENT_NAME);
    }


    @Bean
    public RouteBuilder kafkaHttpKafkaRoute(ValidatedKafkaProducerConfiguration kafkaOutConfiguration,
                                            HttpConsumerConfiguration httpConsumerConfiguration) {

        EndpointProducerBuilder kafkaOut = kafka(ComponentNameConstants.KAFKA_OUT_COMPONENT_NAME, kafkaOutConfiguration.getTopic());
        kafkaOutConfiguration.getEndpointProperties().ifPresent(additional -> additional.forEach(kafkaOut::doSetProperty));

        return new HttpKafkaRoute(httpConsumerConfiguration, kafkaOut);
    }
}
