package com.bridle.configuration;

import com.bridle.configuration.component.HttpProducerConfiguration;
import com.bridle.properties.SchedulerConsumerConfiguration;
import com.bridle.routes.HttpPollHttpRoute;
import com.bridle.utils.ComponentCustomizerImpl;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.endpoint.StaticEndpointBuilders;
import org.apache.camel.component.http.HttpComponent;
import org.apache.camel.component.scheduler.SchedulerComponent;
import org.apache.camel.spi.ComponentCustomizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import static com.bridle.configuration.ComponentNameConstants.REST_CALL_COMPONENT_NAME;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.http;

@Configuration
@ConditionalOnProperty(name = "gateway.type", havingValue = HttpPollHttpConfiguration.GATEWAY_TYPE_HTTP_POLL_HTTP)
public class HttpPollHttpConfiguration {

    public static final String GATEWAY_TYPE_HTTP_POLL_HTTP = "http-poll-http";

    @ConfigurationProperties(prefix = ComponentNameConstants.SCHEDULER_COMPONENT_NAME)
    @Bean
    public SchedulerConsumerConfiguration schedulerConfiguration() {
        return new SchedulerConsumerConfiguration();
    }

    @Bean(name = ComponentNameConstants.SCHEDULER_COMPONENT_NAME)
    public SchedulerComponent schedulerMainComponent() {
        return new SchedulerComponent();
    }

    @Lazy
    @Bean
    public ComponentCustomizer configureSchedulerComponent(CamelContext context, SchedulerConsumerConfiguration componentConfiguration) {
        return new ComponentCustomizerImpl(context, componentConfiguration, ComponentNameConstants.SCHEDULER_COMPONENT_NAME);
    }

    @ConfigurationProperties(prefix = ComponentNameConstants.REST_POLL_COMPONENT_NAME)
    @Bean
    public HttpProducerConfiguration restPollConfiguration() {
        return new HttpProducerConfiguration();
    }

    @Bean(name = ComponentNameConstants.REST_POLL_COMPONENT_NAME)
    @Lazy
    public HttpComponent restPollComponent() {
        return new HttpComponent();
    }

    @Lazy
    @Bean
    public ComponentCustomizer configureHttpPollComponent(CamelContext context, @Qualifier("restPollConfiguration") HttpProducerConfiguration componentConfiguration) {
        return new ComponentCustomizerImpl(context, componentConfiguration, ComponentNameConstants.REST_POLL_COMPONENT_NAME);
    }

    @ConfigurationProperties(prefix = REST_CALL_COMPONENT_NAME)
    @Bean
    public HttpProducerConfiguration restCallConfiguration() {
        return new HttpProducerConfiguration();
    }

    @Bean(name = REST_CALL_COMPONENT_NAME)
    @Lazy
    public HttpComponent restCallComponent() {
        return new HttpComponent();
    }

    @Lazy
    @Bean
    public ComponentCustomizer configureHttpComponent(CamelContext context, @Qualifier("restCallConfiguration") HttpProducerConfiguration componentConfiguration) {
        return new ComponentCustomizerImpl(context, componentConfiguration, REST_CALL_COMPONENT_NAME);
    }

    @Bean
    public RouteBuilder httpPollHttpRoute(SchedulerConsumerConfiguration schedulerConfiguration,
                                          @Qualifier("restPollConfiguration") HttpProducerConfiguration restPollConfiguration,
                                          @Qualifier("restCallConfiguration") HttpProducerConfiguration restCallConfiguration) {

        EndpointConsumerBuilder scheduler = StaticEndpointBuilders.scheduler(ComponentNameConstants.SCHEDULER_COMPONENT_NAME, ComponentNameConstants.SCHEDULER_COMPONENT_NAME);
        schedulerConfiguration.getEndpointProperties().ifPresent(additional -> additional.forEach(scheduler::doSetProperty));

        EndpointProducerBuilder restPoll = http(ComponentNameConstants.REST_POLL_COMPONENT_NAME, restPollConfiguration.createHttpUrl());
        restPollConfiguration.getEndpointProperties().ifPresent(additional -> additional.forEach(restPoll::doSetProperty));

        EndpointProducerBuilder restCall = http(REST_CALL_COMPONENT_NAME, restCallConfiguration.createHttpUrl());
        restCallConfiguration.getEndpointProperties().ifPresent(additional -> additional.forEach(restCall::doSetProperty));

        return new HttpPollHttpRoute(scheduler, restPoll, restCall);
    }
}
