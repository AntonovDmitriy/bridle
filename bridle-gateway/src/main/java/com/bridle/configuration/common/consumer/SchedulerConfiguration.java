package com.bridle.configuration.common.consumer;

import com.bridle.properties.SchedulerConsumerConfiguration;
import com.bridle.utils.ComponentCustomizerImpl;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.endpoint.StaticEndpointBuilders;
import org.apache.camel.component.scheduler.SchedulerComponent;
import org.apache.camel.spi.ComponentCustomizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import static com.bridle.configuration.common.ComponentNameConstants.SCHEDULER_COMPONENT_NAME;

public class SchedulerConfiguration {

    @ConfigurationProperties(prefix = SCHEDULER_COMPONENT_NAME)
    @Bean
    public SchedulerConsumerConfiguration schedulerConfiguration() {
        return new SchedulerConsumerConfiguration();
    }

    @Bean(name = SCHEDULER_COMPONENT_NAME)
    public SchedulerComponent schedulerMainComponent() {
        return new SchedulerComponent();
    }

    @Lazy
    @Bean
    public ComponentCustomizer configureSchedulerComponent(CamelContext context,
                                                           SchedulerConsumerConfiguration componentConfiguration) {
        return new ComponentCustomizerImpl(context, componentConfiguration, SCHEDULER_COMPONENT_NAME);
    }

    @Bean
    public EndpointConsumerBuilder schedulerConsumer(SchedulerConsumerConfiguration configuration) {
        EndpointConsumerBuilder result =
                StaticEndpointBuilders.scheduler(SCHEDULER_COMPONENT_NAME, SCHEDULER_COMPONENT_NAME);
        configuration.getEndpointProperties().ifPresent(additional -> additional.forEach(result::doSetProperty));
        return result;
    }

}
