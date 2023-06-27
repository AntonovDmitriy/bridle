package com.bridle.configuration.routes;

import com.bridle.configuration.common.ErrorHandlerConfiguration;
import com.bridle.configuration.common.HttpPollConfiguration;
import com.bridle.configuration.common.RestCallConfiguration;
import com.bridle.configuration.common.SchedulerConfiguration;
import com.bridle.properties.HttpProducerConfiguration;
import com.bridle.properties.SchedulerConsumerConfiguration;
import com.bridle.routes.HttpPollHttpRoute;
import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.endpoint.StaticEndpointBuilders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static com.bridle.configuration.common.ComponentNameConstants.REST_CALL_COMPONENT_NAME;
import static com.bridle.configuration.common.ComponentNameConstants.REST_POLL_COMPONENT_NAME;
import static com.bridle.configuration.common.ComponentNameConstants.SCHEDULER_COMPONENT_NAME;
import static com.bridle.configuration.routes.HttpPollHttpConfiguration.GATEWAY_TYPE_HTTP_POLL_HTTP;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.http;

@Configuration
@Import({SchedulerConfiguration.class,
        HttpPollConfiguration.class,
        RestCallConfiguration.class,
        ErrorHandlerConfiguration.class})
@ConditionalOnProperty(name = "gateway.type", havingValue = GATEWAY_TYPE_HTTP_POLL_HTTP)
public class HttpPollHttpConfiguration {

    public static final String GATEWAY_TYPE_HTTP_POLL_HTTP = "http-poll-http";

    @Bean
    public RouteBuilder httpPollHttpRoute(ErrorHandlerFactory errorHandlerFactory,
                                          SchedulerConsumerConfiguration schedulerConfiguration,
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

        return new HttpPollHttpRoute(errorHandlerFactory, scheduler, restPoll, restCall);
    }
}
