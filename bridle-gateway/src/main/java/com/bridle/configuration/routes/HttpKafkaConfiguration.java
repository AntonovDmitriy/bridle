package com.bridle.configuration.routes;

import com.bridle.configuration.common.errorhandling.ErrorHandlerConfiguration;
import com.bridle.configuration.common.errorhandling.ErrorResponseConfiguration;
import com.bridle.configuration.common.errorhandling.ValidationErrorResponseConfiguration;
import com.bridle.configuration.common.processing.AfterConsumerProcessingConfiguration;
import com.bridle.configuration.common.producer.KafkaOutConfiguration;
import com.bridle.configuration.common.producer.SuccessResponseConfiguration;
import com.bridle.properties.HttpConsumerConfiguration;
import com.bridle.routes.HttpConsumerToProducerRoute;
import com.bridle.routes.HttpConsumerToProducerRouteParams;
import com.bridle.routes.ProcessingParams;
import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static com.bridle.configuration.common.ComponentNameConstants.REST_IN_COMPONENT_NAME;

@Configuration
@Import({KafkaOutConfiguration.class, ErrorHandlerConfiguration.class, AfterConsumerProcessingConfiguration.class,
        SuccessResponseConfiguration.class, ErrorResponseConfiguration.class,
        ValidationErrorResponseConfiguration.class})
@ConditionalOnProperty(name = "gateway.type",
        havingValue = HttpKafkaConfiguration.GATEWAY_TYPE_HTTP_KAFKA)
public class HttpKafkaConfiguration {

    public static final String GATEWAY_TYPE_HTTP_KAFKA = "http-kafka";

    @ConfigurationProperties(prefix = REST_IN_COMPONENT_NAME)
    @Bean
    public HttpConsumerConfiguration restInConfiguration() {
        return new HttpConsumerConfiguration();
    }

    @Bean
    public RouteBuilder kafkaHttpKafkaRoute(EndpointProducerBuilder kafkaProducerBuilder,
            ErrorHandlerFactory errorHandlerFactory,
            HttpConsumerConfiguration httpConsumerConfiguration,
            @Autowired(required = false)
            @Qualifier("afterConsumer")
            ProcessingParams processingAfterConsumerParams,
            @Autowired(required = false)
            @Qualifier("afterProducer")
            ProcessingParams processingAfterProducerParams,
            @Qualifier("successResponseBuilder")
            EndpointProducerBuilder successResponseBuilder,
            @Qualifier("errorResponseBuilder")
            EndpointProducerBuilder errorResponseBuilder,
            @Qualifier("validationErrorResponseBuilder")
            EndpointProducerBuilder validationErrorResponseBuilder) {

        return new HttpConsumerToProducerRoute(errorHandlerFactory,
                                               httpConsumerConfiguration,
                                               new HttpConsumerToProducerRouteParams(GATEWAY_TYPE_HTTP_KAFKA,
                                                                                     kafkaProducerBuilder,
                                                                                     successResponseBuilder,
                                                                                     errorResponseBuilder,
                                                                                     validationErrorResponseBuilder,
                                                                                     processingAfterConsumerParams,
                                                                                     processingAfterProducerParams));
    }
}
