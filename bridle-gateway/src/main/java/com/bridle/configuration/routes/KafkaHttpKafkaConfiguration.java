package com.bridle.configuration.routes;

import com.bridle.configuration.common.DynamicComponentsComfiguration;
import com.bridle.configuration.common.errorhandling.ErrorHandlerConfiguration;
import com.bridle.configuration.common.processing.AfterConsumerProcessingConfiguration;
import com.bridle.configuration.common.processing.AfterProducerProcessingConfiguration;
import com.bridle.routes.ConsumerToDoubleProducerRoute;
import com.bridle.routes.model.ConsumerToDoubleProducerRouteParams;
import com.bridle.utils.ProcessingParams;
import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static com.bridle.configuration.routes.KafkaHttpKafkaConfiguration.GATEWAY_TYPE_KAFKA_HTTP_KAFKA;

@Configuration
@Import({ErrorHandlerConfiguration.class, AfterConsumerProcessingConfiguration.class,
        AfterProducerProcessingConfiguration.class, DynamicComponentsComfiguration.class})
@ConditionalOnProperty(name = "gateway.type",
        havingValue = GATEWAY_TYPE_KAFKA_HTTP_KAFKA)
public class KafkaHttpKafkaConfiguration {

    public static final String GATEWAY_TYPE_KAFKA_HTTP_KAFKA = "kafka-http-kafka";


    @Bean
    public RouteBuilder kafkaHttpKafkaRoute(ErrorHandlerFactory errorHandlerFactory,
                                            @Qualifier("kafka-in-endpoint")
                                            EndpointConsumerBuilder kafkaInEndpoint,
                                            @Qualifier("rest-call-endpoint")
                                            EndpointProducerBuilder restCall,
                                            @Qualifier("kafka-out-endpoint")
                                            EndpointProducerBuilder kafkaOutEndpoint,
                                            @Autowired(required = false)
                                            @Qualifier("afterConsumer")
                                            ProcessingParams processingAfterConsumerParams,
                                            @Autowired(required = false)
                                            @Qualifier("afterProducer")
                                            ProcessingParams processingAfterProducerParams) {
        return new ConsumerToDoubleProducerRoute(errorHandlerFactory,
                new ConsumerToDoubleProducerRouteParams(GATEWAY_TYPE_KAFKA_HTTP_KAFKA,
                        kafkaInEndpoint,
                        processingAfterConsumerParams,
                        restCall,
                        processingAfterProducerParams,
                        kafkaOutEndpoint));
    }
}
