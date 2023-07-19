package com.bridle.configuration.common.processing;

import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.model.dataformat.JacksonXMLDataFormat;
import org.apache.camel.model.dataformat.JsonDataFormat;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

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
