package com.bridle.configuration.common;

import com.bridle.properties.ValidatedKafkaProducerConfiguration;
import com.bridle.utils.ComponentCustomizerImpl;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.model.dataformat.JacksonXMLDataFormat;
import org.apache.camel.model.dataformat.JsonDataFormat;
import org.apache.camel.spi.ComponentCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import static com.bridle.configuration.common.ComponentNameConstants.JSON_VALIDATOR_COMPONENT_NAME;
import static com.bridle.configuration.common.ComponentNameConstants.KAFKA_OUT_COMPONENT_NAME;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.kafka;


@Import({JsonSchemaValidationConfiguration.class})
public class InboundValidationConfiguration {

}
