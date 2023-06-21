package com.bridle.configuration;

import com.bridle.properties.HttpProducerConfiguration;
import com.bridle.properties.SchedulerConsumerConfiguration;
import com.bridle.routes.HttpPollHttpRoute;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.endpoint.StaticEndpointBuilders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static com.bridle.configuration.ComponentNameConstants.REST_CALL_COMPONENT_NAME;
import static com.bridle.configuration.ComponentNameConstants.REST_POLL_COMPONENT_NAME;
import static com.bridle.configuration.ComponentNameConstants.SCHEDULER_COMPONENT_NAME;
import static com.bridle.configuration.HttpPollHttpConfiguration.GATEWAY_TYPE_HTTP_POLL_HTTP;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.http;

@Configuration
@Import({SchedulerConfiguration.class, HttpPollConfiguration.class, RestCallConfiguration.class})
@ConditionalOnProperty(name = "gateway.type", havingValue = GATEWAY_TYPE_HTTP_POLL_HTTP)
public class HttpPollHttpConfiguration {

    public static final String GATEWAY_TYPE_HTTP_POLL_HTTP = "http-poll-http";

    @Bean
    public RouteBuilder httpPollHttpRoute(SchedulerConsumerConfiguration schedulerConfiguration,
                                          @Qualifier("restPollConfiguration")
                                          HttpProducerConfiguration restPollConfiguration,
                                          @Qualifier("restCallConfiguration")
                                          HttpProducerConfiguration restCallConfiguration) {

        EndpointConsumerBuilder scheduler = StaticEndpointBuilders.scheduler(SCHEDULER_COMPONENT_NAME,
                SCHEDULER_COMPONENT_NAME);
        schedulerConfiguration.getEndpointProperties()
                .ifPresent(additional -> additional.forEach(scheduler::doSetProperty));

        EndpointProducerBuilder restPoll = http(REST_POLL_COMPONENT_NAME, restPollConfiguration.createHttpUrl());
        restPollConfiguration.getEndpointProperties()
                .ifPresent(additional -> additional.forEach(restPoll::doSetProperty));

        EndpointProducerBuilder restCall = http(REST_CALL_COMPONENT_NAME, restCallConfiguration.createHttpUrl());
        restCallConfiguration.getEndpointProperties().
                ifPresent(additional -> additional.forEach(restCall::doSetProperty));

        return new HttpPollHttpRoute(scheduler, restPoll, restCall);
    }
}
