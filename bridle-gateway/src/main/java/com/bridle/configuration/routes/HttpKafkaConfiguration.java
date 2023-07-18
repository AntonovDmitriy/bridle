package com.bridle.configuration.routes;

import com.bridle.configuration.common.ErrorHandlerConfiguration;
import com.bridle.configuration.common.ErrorResponseConfiguration;
import com.bridle.configuration.common.FreemarkerConfiguration;
import com.bridle.configuration.common.HeaderCollectorConfiguration;
import com.bridle.configuration.common.InboundDataFormatConfiguration;
import com.bridle.configuration.common.InboundValidationConfiguration;
import com.bridle.configuration.common.KafkaOutConfiguration;
import com.bridle.configuration.common.SuccessResponseConfiguration;
import com.bridle.configuration.common.ValidationErrorResponseConfiguration;
import com.bridle.properties.HttpConsumerConfiguration;
import com.bridle.routes.HttpKafkaRoute;
import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.Processor;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.DataFormatDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static com.bridle.configuration.common.ComponentNameConstants.HEADER_COLLECTOR_COMPONENT_NAME;
import static com.bridle.configuration.common.ComponentNameConstants.REST_IN_COMPONENT_NAME;

@Configuration
@Import({KafkaOutConfiguration.class,
        ErrorHandlerConfiguration.class,
        SuccessResponseConfiguration.class,
        ErrorResponseConfiguration.class,
        FreemarkerConfiguration.class,
        HeaderCollectorConfiguration.class,
        InboundDataFormatConfiguration.class,
        InboundValidationConfiguration.class,
        ValidationErrorResponseConfiguration.class
})
@ConditionalOnProperty(name = "gateway.type", havingValue = HttpKafkaConfiguration.GATEWAY_TYPE_HTTP_KAFKA)
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
                                            @Qualifier("successResponseBuilder")
                                            EndpointProducerBuilder successResponseBuilder,
                                            @Qualifier("errorResponseBuilder")
                                            EndpointProducerBuilder errorResponseBuilder,
                                            @Autowired(required = false)
                                            @Qualifier("freemarkerTransformBuilder")
                                            EndpointProducerBuilder transform,
                                            @Qualifier(HEADER_COLLECTOR_COMPONENT_NAME)
                                            Processor headerCollector,
                                            @Autowired(required = false)
                                            @Qualifier("inboundDataFormat")
                                            DataFormatDefinition inboundDataFormat,
                                            @Autowired(required = false)
                                            @Qualifier("validatorBuilder")
                                            EndpointProducerBuilder inboundValidator,
                                            @Qualifier("validationErrorResponseBuilder")
                                            EndpointProducerBuilder validationErrorResponseBuilder) {

        return new HttpKafkaRoute(errorHandlerFactory,
                httpConsumerConfiguration,
                new RouteParams(
                        kafkaProducerBuilder,
                        successResponseBuilder,
                        errorResponseBuilder,
                        transform,
                        headerCollector,
                        inboundDataFormat,
                        null,
                        inboundValidator,
                        validationErrorResponseBuilder,
                        null));
    }
}
