package com.bridle.configuration.routes;

import com.bridle.configuration.common.ErrorHandlerConfiguration;
import com.bridle.configuration.common.ErrorResponseConfiguration;
import com.bridle.configuration.common.FreemarkerConfiguration;
import com.bridle.configuration.common.HeaderCollectorConfiguration;
import com.bridle.configuration.common.KafkaOutConfiguration;
import com.bridle.configuration.common.SuccessResponseConfiguration;
import com.bridle.properties.FreemarkerProducerConfiguration;
import com.bridle.properties.HttpConsumerConfiguration;
import com.bridle.properties.ValidatedKafkaProducerConfiguration;
import com.bridle.routes.HttpToProducerRoute;
import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.Processor;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static com.bridle.configuration.common.ComponentNameConstants.ERROR_RESPONSE_FREEMARKER_COMPONENT_NAME;
import static com.bridle.configuration.common.ComponentNameConstants.FREEMARKER_COMPONENT_NAME;
import static com.bridle.configuration.common.ComponentNameConstants.HEADER_COLLECTOR_COMPONENT_NAME;
import static com.bridle.configuration.common.ComponentNameConstants.KAFKA_OUT_COMPONENT_NAME;
import static com.bridle.configuration.common.ComponentNameConstants.REST_IN_COMPONENT_NAME;
import static com.bridle.configuration.common.ComponentNameConstants.SUCCESS_RESPONSE_FREEMARKER_COMPONENT_NAME;
import static com.bridle.configuration.common.EndpointBuilders.buildFreemarker;
import static com.bridle.configuration.common.EndpointBuilders.buildKafkaProducer;

@Configuration
@Import({KafkaOutConfiguration.class,
        ErrorHandlerConfiguration.class,
        SuccessResponseConfiguration.class,
        ErrorResponseConfiguration.class,
        FreemarkerConfiguration.class,
        HeaderCollectorConfiguration.class})
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
                                            FreemarkerProducerConfiguration errorResponseConfiguration,
                                            @Qualifier("freemarkerConfiguration")
                                            FreemarkerProducerConfiguration transformConfiguration,
                                            @Qualifier(HEADER_COLLECTOR_COMPONENT_NAME)
                                            Processor headerCollector) {

        EndpointProducerBuilder mainProducer = buildKafkaProducer(KAFKA_OUT_COMPONENT_NAME, kafkaOutConfiguration);
        EndpointProducerBuilder successResponseBuilder = buildFreemarker(SUCCESS_RESPONSE_FREEMARKER_COMPONENT_NAME,
                successResponseConfiguration);
        EndpointProducerBuilder errorResponseBuilder = buildFreemarker(ERROR_RESPONSE_FREEMARKER_COMPONENT_NAME,
                errorResponseConfiguration);
        EndpointProducerBuilder transform = buildFreemarker(FREEMARKER_COMPONENT_NAME, transformConfiguration);

        return new HttpToProducerRoute(errorHandlerFactory,
                httpConsumerConfiguration,
                mainProducer,
                successResponseBuilder,
                errorResponseBuilder,
                transform,
                headerCollector,
                null,
                null,
                null);
    }
}
