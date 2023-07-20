package com.bridle.configuration.routes;

import com.bridle.configuration.common.consumer.KafkaInConfiguration;
import com.bridle.configuration.common.errorhandling.ErrorHandlerConfiguration;
import com.bridle.configuration.common.processing.AfterConsumerProcessingConfiguration;
import com.bridle.configuration.common.processing.AfterProducerProcessingConfiguration;
import com.bridle.configuration.common.producer.RestCallConfiguration;
import com.bridle.routes.ConsumerToProducerRoute;
import com.bridle.routes.ConsumerToProducerRouteParams;
import com.bridle.routes.ProcessingParams;
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

import static com.bridle.configuration.routes.KafkaHttpConfiguration.GATEWAY_TYPE_KAFKA_HTTP;

@Configuration
@Import({KafkaInConfiguration.class, RestCallConfiguration.class, ErrorHandlerConfiguration.class,
        AfterConsumerProcessingConfiguration.class, AfterProducerProcessingConfiguration.class})
@ConditionalOnProperty(name = "gateway.type",
        havingValue = GATEWAY_TYPE_KAFKA_HTTP)
public class KafkaHttpConfiguration {

    public static final String GATEWAY_TYPE_KAFKA_HTTP = "kafka-http";

    @Bean
    public RouteBuilder kafkaHttpRoute(ErrorHandlerFactory errorHandlerFactory,
            EndpointConsumerBuilder kafkaConsumerBuilder,
            @Qualifier("restCallBuilder")
            EndpointProducerBuilder restCall,
            @Autowired(required = false)
            @Qualifier("afterConsumer")
            ProcessingParams processingAfterConsumerParams,
            @Autowired(required = false)
            @Qualifier("afterProducer")
            ProcessingParams processingAfterProducerParams) {
        return new ConsumerToProducerRoute(errorHandlerFactory,
                                           new ConsumerToProducerRouteParams(GATEWAY_TYPE_KAFKA_HTTP,
                                                                             kafkaConsumerBuilder,
                                                                             processingAfterConsumerParams,
                                                                             restCall,
                                                                             processingAfterProducerParams));
    }
}
