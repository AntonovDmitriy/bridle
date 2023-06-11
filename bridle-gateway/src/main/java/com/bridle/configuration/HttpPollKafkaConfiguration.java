package com.bridle.configuration;

import com.bridle.configuration.component.HttpProducerConfiguration;
import com.bridle.properties.SchedulerConsumerConfiguration;
import com.bridle.properties.ValidatedKafkaProducerConfiguration;
import com.bridle.routes.HttpPollHttpRoute;
import com.bridle.routes.HttpPollKafkaRoute;
import com.bridle.utils.ComponentCustomizerImpl;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.endpoint.StaticEndpointBuilders;
import org.apache.camel.component.http.HttpComponent;
import org.apache.camel.component.kafka.KafkaComponent;
import org.apache.camel.component.scheduler.SchedulerComponent;
import org.apache.camel.spi.ComponentCustomizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import static com.bridle.configuration.ComponentNameConstants.*;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.http;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.kafka;

@Configuration
@ConditionalOnProperty(name = "gateway.type", havingValue = HttpPollKafkaConfiguration.GATEWAY_TYPE_HTTP_POLL_KAFKA)
public class HttpPollKafkaConfiguration {
    public static final String GATEWAY_TYPE_HTTP_POLL_KAFKA = "http-poll-kafka";

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
    public ComponentCustomizer configureSchedulerComponent(CamelContext context, SchedulerConsumerConfiguration componentConfiguration) {
        return new ComponentCustomizerImpl(context, componentConfiguration, SCHEDULER_COMPONENT_NAME);
    }

    @ConfigurationProperties(prefix = REST_POLL_COMPONENT_NAME)
    @Bean
    public HttpProducerConfiguration restPollConfiguration() {
        return new HttpProducerConfiguration();
    }

    @Bean(name = REST_POLL_COMPONENT_NAME)
    @Lazy
    public HttpComponent restPollComponent() {
        return new HttpComponent();
    }

    @Lazy
    @Bean
    public ComponentCustomizer configureHttpPollComponent(CamelContext context, HttpProducerConfiguration componentConfiguration) {
        return new ComponentCustomizerImpl(context, componentConfiguration, REST_POLL_COMPONENT_NAME);
    }

    @ConfigurationProperties(prefix = KAFKA_OUT_COMPONENT_NAME)
    @Bean
    public ValidatedKafkaProducerConfiguration kafkaOutConfiguration() {
        return new ValidatedKafkaProducerConfiguration();
    }

    @Bean(name = KAFKA_OUT_COMPONENT_NAME)
    @Lazy
    public KafkaComponent kafkaOutComponent() {
        return new KafkaComponent();
    }

    @Lazy
    @Bean
    public ComponentCustomizer configureKafkaOutComponent(CamelContext context,  ValidatedKafkaProducerConfiguration componentConfiguration) {
        return new ComponentCustomizerImpl(context, componentConfiguration, KAFKA_OUT_COMPONENT_NAME);
    }

    @Bean
    public RouteBuilder httpPollHttpRoute(SchedulerConsumerConfiguration schedulerConfiguration,
                                          HttpProducerConfiguration restPollConfiguration,
                                           ValidatedKafkaProducerConfiguration kafkaOutConfiguration) {

        EndpointConsumerBuilder scheduler = StaticEndpointBuilders.scheduler(SCHEDULER_COMPONENT_NAME, SCHEDULER_COMPONENT_NAME);
        schedulerConfiguration.getEndpointProperties().ifPresent(additional -> additional.forEach(scheduler::doSetProperty));

        EndpointProducerBuilder restPoll = http(REST_POLL_COMPONENT_NAME, restPollConfiguration.createHttpUrl());
        restPollConfiguration.getEndpointProperties().ifPresent(additional -> additional.forEach(restPoll::doSetProperty));

        EndpointProducerBuilder kafka = kafka(KAFKA_OUT_COMPONENT_NAME, kafkaOutConfiguration.getTopic());
        kafkaOutConfiguration.getEndpointProperties().ifPresent(additional -> additional.forEach(kafka::doSetProperty));

        return new HttpPollKafkaRoute(scheduler, restPoll, kafka);
    }
}
