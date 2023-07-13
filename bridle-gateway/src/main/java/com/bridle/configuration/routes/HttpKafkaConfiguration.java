package com.bridle.configuration.routes;

import com.bridle.configuration.common.ErrorHandlerConfiguration;
import com.bridle.configuration.common.ErrorResponseConfiguration;
import com.bridle.configuration.common.KafkaOutConfiguration;
import com.bridle.configuration.common.SuccessResponseConfiguration;
import com.bridle.properties.FreemarkerProducerConfiguration;
import com.bridle.properties.HttpConsumerConfiguration;
import com.bridle.properties.ValidatedKafkaProducerConfiguration;
import com.bridle.routes.HttpKafkaRoute;
import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.endpoint.StaticEndpointBuilders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static com.bridle.configuration.common.ComponentNameConstants.ERROR_RESPONSE_FREEMARKER_COMPONENT_NAME;
import static com.bridle.configuration.common.ComponentNameConstants.KAFKA_OUT_COMPONENT_NAME;
import static com.bridle.configuration.common.ComponentNameConstants.REST_IN_COMPONENT_NAME;
import static com.bridle.configuration.common.ComponentNameConstants.SUCCESS_RESPONSE_FREEMARKER_COMPONENT_NAME;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.kafka;

@Configuration
@Import({KafkaOutConfiguration.class,
        ErrorHandlerConfiguration.class,
        SuccessResponseConfiguration.class,
        ErrorResponseConfiguration.class})
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
                                            HttpConsumerConfiguration httpConsumerConfiguration,
                                            @Qualifier("successResponseConfiguration")
                                            FreemarkerProducerConfiguration successResponseConfiguration,
                                            @Qualifier("errorResponseConfiguration")
                                            FreemarkerProducerConfiguration errorResponseConfiguration) {

        EndpointProducerBuilder kafkaOut = kafka(KAFKA_OUT_COMPONENT_NAME, kafkaOutConfiguration.getTopic());
        kafkaOutConfiguration.getEndpointProperties()
                .ifPresent(additional -> additional.forEach(kafkaOut::doSetProperty));

        EndpointProducerBuilder successResponseBuilder = StaticEndpointBuilders.freemarker(SUCCESS_RESPONSE_FREEMARKER_COMPONENT_NAME,
                successResponseConfiguration.getResourceUri());
        successResponseConfiguration.getEndpointProperties()
                .ifPresent(additional -> additional.forEach(successResponseBuilder::doSetProperty));

        EndpointProducerBuilder errorResponseBuilder = StaticEndpointBuilders.freemarker(ERROR_RESPONSE_FREEMARKER_COMPONENT_NAME,
                errorResponseConfiguration.getResourceUri());
        errorResponseConfiguration.getEndpointProperties()
                .ifPresent(additional -> additional.forEach(errorResponseBuilder::doSetProperty));

        return new HttpKafkaRoute(errorHandlerFactory,
                httpConsumerConfiguration,
                kafkaOut,
                successResponseBuilder,
                errorResponseBuilder);
    }
}
