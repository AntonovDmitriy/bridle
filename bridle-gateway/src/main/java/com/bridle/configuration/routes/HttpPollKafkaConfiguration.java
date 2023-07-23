package com.bridle.configuration.routes;

import com.bridle.configuration.common.consumer.SchedulerConfiguration;
import com.bridle.configuration.common.errorhandling.ErrorHandlerConfiguration;
import com.bridle.configuration.common.processing.AfterConsumerProcessingConfiguration;
import com.bridle.configuration.common.processing.AfterProducerProcessingConfiguration;
import com.bridle.configuration.common.producer.HttpPollConfiguration;
import com.bridle.configuration.common.producer.KafkaOutConfiguration;
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

import static com.bridle.configuration.routes.HttpPollKafkaConfiguration.GATEWAY_TYPE_HTTP_POLL_KAFKA;

@Configuration
@Import({SchedulerConfiguration.class, HttpPollConfiguration.class, KafkaOutConfiguration.class,
        ErrorHandlerConfiguration.class, AfterConsumerProcessingConfiguration.class,
        AfterProducerProcessingConfiguration.class})
@ConditionalOnProperty(name = "gateway.type",
        havingValue = GATEWAY_TYPE_HTTP_POLL_KAFKA)
public class HttpPollKafkaConfiguration {

    public static final String GATEWAY_TYPE_HTTP_POLL_KAFKA = "http-poll-kafka";

    @Bean
    public RouteBuilder httpPollKafkaRoute(ErrorHandlerFactory errorHandlerFactory,
            @Qualifier("schedulerConsumerBuilder")
            EndpointConsumerBuilder scheduler,
            @Qualifier("restPollBuilder")
            EndpointProducerBuilder restPoll,
            EndpointProducerBuilder kafkaProducerBuilder,
            @Autowired(required = false)
            @Qualifier("afterConsumer")
            ProcessingParams processingAfterConsumerParams,
            @Autowired(required = false)
            @Qualifier("afterProducer")
            ProcessingParams processingAfterProducerParams) {
        return new ConsumerToDoubleProducerRoute(errorHandlerFactory,
                                                 new ConsumerToDoubleProducerRouteParams(GATEWAY_TYPE_HTTP_POLL_KAFKA,
                                                                                         scheduler,
                                                                                         processingAfterConsumerParams,
                                                                                         restPoll,
                                                                                         processingAfterProducerParams,
                                                                                         kafkaProducerBuilder));
    }
}
