package com.bridle.routes;

import com.bridle.properties.HttpConsumerConfiguration;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ToDefinition;
import org.apache.camel.model.rest.RestDefinition;
import org.apache.camel.model.rest.RestsDefinition;
import org.apache.camel.spi.RestConfiguration;

import java.util.List;

import static com.bridle.configuration.HttpKafkaConfiguration.GATEWAY_TYPE_HTTP_KAFKA;
import static com.bridle.configuration.HttpPollKafkaConfiguration.GATEWAY_TYPE_HTTP_POLL_KAFKA;

public class HttpKafkaRoute extends GenericHttpConsumerRoute {

    private final EndpointProducerBuilder kafkaOut;

    public HttpKafkaRoute(HttpConsumerConfiguration restConfiguration, EndpointProducerBuilder kafkaOut) {
        super(restConfiguration);
        this.kafkaOut = kafkaOut;
    }

    @Override
    public void configure() throws Exception {
        super.configure();

        from("direct:process")
            .routeId(GATEWAY_TYPE_HTTP_KAFKA)
            .to(kafkaOut)
            .setBody(constant("Success!"))
            .log("Response: ${body}");
    }
}
