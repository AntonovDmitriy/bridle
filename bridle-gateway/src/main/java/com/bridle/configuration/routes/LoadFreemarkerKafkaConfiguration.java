package com.bridle.configuration.routes;

import com.bridle.configuration.common.consumer.SchedulerConfiguration;
import com.bridle.configuration.common.errorhandling.ErrorHandlerConfiguration;
import com.bridle.configuration.common.processing.FreemarkerConfiguration;
import com.bridle.configuration.common.producer.KafkaOutConfiguration;
import com.bridle.properties.FreemarkerProducerConfiguration;
import com.bridle.properties.SchedulerConsumerConfiguration;
import com.bridle.properties.ValidatedKafkaProducerConfiguration;
import com.bridle.routes.LoadFreemarkerKafkaRoute;
import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.endpoint.StaticEndpointBuilders;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static com.bridle.configuration.common.ComponentNameConstants.FREEMARKER_COMPONENT_NAME;
import static com.bridle.configuration.common.ComponentNameConstants.KAFKA_OUT_COMPONENT_NAME;
import static com.bridle.configuration.common.ComponentNameConstants.SCHEDULER_COMPONENT_NAME;
import static com.bridle.configuration.routes.LoadFreemarkerKafkaConfiguration.LOAD_FREEMARKER_KAFKA;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.kafka;

@Configuration
@Import({SchedulerConfiguration.class,
        KafkaOutConfiguration.class,
        FreemarkerConfiguration.class,
        ErrorHandlerConfiguration.class})
@ConditionalOnProperty(name = "gateway.type", havingValue = LOAD_FREEMARKER_KAFKA)
public class LoadFreemarkerKafkaConfiguration {

    public static final String LOAD_FREEMARKER_KAFKA = "load-freemarker-kafka";

    @Bean
    public RouteBuilder dataSetHttpRoute(ErrorHandlerFactory errorHandlerFactory,
                                         ValidatedKafkaProducerConfiguration kafkaProducerConfiguration,
                                         FreemarkerProducerConfiguration freemarkerConfiguration,
                                         SchedulerConsumerConfiguration schedulerConfiguration) {

        EndpointConsumerBuilder scheduler = StaticEndpointBuilders.scheduler(SCHEDULER_COMPONENT_NAME,
                SCHEDULER_COMPONENT_NAME);
        schedulerConfiguration.getEndpointProperties()
                .ifPresent(additional -> additional.forEach(scheduler::doSetProperty));

        EndpointProducerBuilder freemarker = StaticEndpointBuilders.freemarker(FREEMARKER_COMPONENT_NAME,
                freemarkerConfiguration.getResourceUri());
        freemarkerConfiguration.getEndpointProperties()
                .ifPresent(additional -> additional.forEach(freemarker::doSetProperty));

        EndpointProducerBuilder kafkaOut = kafka(KAFKA_OUT_COMPONENT_NAME, kafkaProducerConfiguration.getTopic());
        kafkaProducerConfiguration.getEndpointProperties().
                ifPresent(additional -> additional.forEach(kafkaOut::doSetProperty));

        return new LoadFreemarkerKafkaRoute(errorHandlerFactory, scheduler, freemarker, kafkaOut);
    }
}
