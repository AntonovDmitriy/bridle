package com.bridle.configuration;

import com.bridle.properties.FreemarkerProducerConfiguration;
import com.bridle.properties.HttpProducerConfiguration;
import com.bridle.properties.SchedulerConsumerConfiguration;
import com.bridle.routes.LoadFreemarkerHttpRoute;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.endpoint.StaticEndpointBuilders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static com.bridle.configuration.ComponentNameConstants.FREEMARKER_COMPONENT_NAME;
import static com.bridle.configuration.ComponentNameConstants.REST_CALL_COMPONENT_NAME;
import static com.bridle.configuration.ComponentNameConstants.SCHEDULER_COMPONENT_NAME;
import static com.bridle.configuration.LoadFreemarkerHttpConfiguration.LOAD_FREEMARKER_HTTP;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.http;

@Configuration
@Import({SchedulerConfiguration.class, RestCallConfiguration.class, FreemarkerConfiguration.class})
@ConditionalOnProperty(name = "gateway.type", havingValue = LOAD_FREEMARKER_HTTP)
public class LoadFreemarkerHttpConfiguration {

    public static final String LOAD_FREEMARKER_HTTP = "load-freemarker-http";

    @Bean
    public RouteBuilder dataSetHttpRoute(@Qualifier("restCallConfiguration")
                                         HttpProducerConfiguration restCallConfiguration,
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

        EndpointProducerBuilder restCall = http(REST_CALL_COMPONENT_NAME, restCallConfiguration.createHttpUrl());
        restCallConfiguration.getEndpointProperties().
                ifPresent(additional -> additional.forEach(restCall::doSetProperty));

        return new LoadFreemarkerHttpRoute(scheduler, freemarker, restCall);
    }
}
