package com.bridle.configuration.routes;

import com.bridle.configuration.common.consumer.SchedulerConfiguration;
import com.bridle.configuration.common.errorhandling.ErrorHandlerConfiguration;
import com.bridle.configuration.common.producer.HttpPollConfiguration;
import com.bridle.configuration.common.producer.KafkaOutConfiguration;
import com.bridle.properties.HttpProducerConfiguration;
import com.bridle.properties.SchedulerConsumerConfiguration;
import com.bridle.properties.ValidatedKafkaProducerConfiguration;
import com.bridle.routes.HttpPollKafkaRoute;
import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.endpoint.StaticEndpointBuilders;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static com.bridle.configuration.common.ComponentNameConstants.KAFKA_OUT_COMPONENT_NAME;
import static com.bridle.configuration.common.ComponentNameConstants.REST_POLL_COMPONENT_NAME;
import static com.bridle.configuration.common.ComponentNameConstants.SCHEDULER_COMPONENT_NAME;
import static com.bridle.configuration.routes.HttpPollKafkaConfiguration.GATEWAY_TYPE_HTTP_POLL_KAFKA;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.http;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.kafka;

@Configuration
@Import({SchedulerConfiguration.class,
        HttpPollConfiguration.class,
        KafkaOutConfiguration.class,
        ErrorHandlerConfiguration.class})
@ConditionalOnProperty(name = "gateway.type", havingValue = GATEWAY_TYPE_HTTP_POLL_KAFKA)
public class HttpPollKafkaConfiguration {
    public static final String GATEWAY_TYPE_HTTP_POLL_KAFKA = "http-poll-kafka";

    @Bean
    public RouteBuilder httpPollHttpRoute(ErrorHandlerFactory errorHandlerFactory,
                                          SchedulerConsumerConfiguration schedulerConfiguration,
                                          HttpProducerConfiguration restPollConfiguration,
                                          ValidatedKafkaProducerConfiguration kafkaOutConfiguration) {

        EndpointConsumerBuilder scheduler = StaticEndpointBuilders
                .scheduler(SCHEDULER_COMPONENT_NAME, SCHEDULER_COMPONENT_NAME);
        schedulerConfiguration.getEndpointProperties()
                .ifPresent(additional -> additional.forEach(scheduler::doSetProperty));

        EndpointProducerBuilder restPoll = http(REST_POLL_COMPONENT_NAME, restPollConfiguration.createHttpUrl());
        restPollConfiguration.getEndpointProperties()
                .ifPresent(additional -> additional.forEach(restPoll::doSetProperty));

        EndpointProducerBuilder kafka = kafka(KAFKA_OUT_COMPONENT_NAME, kafkaOutConfiguration.getTopic());
        kafkaOutConfiguration.getEndpointProperties()
                .ifPresent(additional -> additional.forEach(kafka::doSetProperty));

        return new HttpPollKafkaRoute(errorHandlerFactory, scheduler, restPoll, kafka);
    }
}
