package com.bridle.configuration.common;

import com.bridle.properties.SchedulerConsumerConfiguration;
import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.model.dataformat.JacksonXMLDataFormat;
import org.apache.camel.model.dataformat.JsonDataFormat;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import static com.bridle.configuration.common.ComponentNameConstants.SCHEDULER_COMPONENT_NAME;

public class InboundDataFormatConfiguration {

    @ConfigurationProperties(prefix = "inbound-data-format")
    @ConditionalOnProperty(name = "inbound-data-format.data-format-name", havingValue = "json")
    @Bean("inboundDataFormat")
    public DataFormatDefinition jsonDataFormat() {
        return new JsonDataFormat();
    }

    @ConfigurationProperties(prefix = "inbound-data-format")
    @ConditionalOnProperty(name = "inbound-data-format.data-format-name", havingValue = "xml")
    @Bean("inboundDataFormat")
    public DataFormatDefinition xmlDataFormat() {
        return new JacksonXMLDataFormat();
    }

}
