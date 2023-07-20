package com.bridle.configuration.routes;

import com.bridle.configuration.common.consumer.SchedulerConfiguration;
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

@Configuration
@Import({SchedulerConfiguration.class, RestCallConfiguration.class, ErrorHandlerConfiguration.class,
        AfterConsumerProcessingConfiguration.class, AfterProducerProcessingConfiguration.class})
@ConditionalOnProperty(name = "gateway.type",
        havingValue = SchedulerHttpConfiguration.GATEWAY_TYPE_SCHEDULER_HTTP)
public class SchedulerHttpConfiguration {

    public static final String GATEWAY_TYPE_SCHEDULER_HTTP = "scheduler-http";

    @Bean
    public RouteBuilder dataSetHttpRoute(ErrorHandlerFactory errorHandlerFactory,
            @Qualifier("schedulerConsumerBuilder")
            EndpointConsumerBuilder scheduler,
            @Qualifier("restCallBuilder")
            EndpointProducerBuilder restCall,
            @Qualifier("afterConsumer")
            ProcessingParams processingAfterConsumerParams,
            @Autowired(required = false)
            @Qualifier("afterProducer")
            ProcessingParams processingAfterProducerParams) {
        return new ConsumerToProducerRoute(errorHandlerFactory,
                                           new ConsumerToProducerRouteParams(GATEWAY_TYPE_SCHEDULER_HTTP,
                                                                             scheduler,
                                                                             processingAfterConsumerParams,
                                                                             restCall,
                                                                             processingAfterProducerParams));
    }
}
