package com.bridle.configuration.routes;

import com.bridle.configuration.common.ErrorHandlerConfiguration;
import com.bridle.configuration.common.HttpPollConfiguration;
import com.bridle.configuration.common.RestCallConfiguration;
import com.bridle.configuration.common.SchedulerConfiguration;
import com.bridle.routes.ConsumerToDoubleProducerRoute;
import com.bridle.routes.ConsumerToDoubleProducerRouteParams;
import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static com.bridle.configuration.routes.HttpPollHttpConfiguration.GATEWAY_TYPE_HTTP_POLL_HTTP;

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
                                          EndpointConsumerBuilder scheduler,
                                          @Qualifier("restPollBuilder")
                                          EndpointProducerBuilder restPoll,
                                          @Qualifier("restCallBuilder")
                                          EndpointProducerBuilder restCall) {

        return new ConsumerToDoubleProducerRoute(errorHandlerFactory,
                new ConsumerToDoubleProducerRouteParams(GATEWAY_TYPE_HTTP_POLL_HTTP,
                        scheduler, null, restPoll, null, restCall));
    }
}
