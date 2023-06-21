package com.bridle.routes;

import com.bridle.properties.HttpConsumerConfiguration;
import org.apache.camel.builder.EndpointProducerBuilder;

import static com.bridle.configuration.HttpKafkaConfiguration.GATEWAY_TYPE_HTTP_KAFKA;

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
