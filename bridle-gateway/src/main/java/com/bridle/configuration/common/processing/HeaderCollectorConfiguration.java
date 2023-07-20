package com.bridle.configuration.common.processing;

import com.bridle.component.collector.FacadeHeaderCollectorConfiguration;
import com.bridle.component.collector.FacadeHeadersCollector;
import com.bridle.component.collector.SingletonHeaderCollectorsFactory;
import com.bridle.component.collector.ValuesCollectorFactory;
import com.bridle.configuration.common.ComponentNameConstants;
import org.apache.camel.Processor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

public class HeaderCollectorConfiguration {

    @ConfigurationProperties(prefix = ComponentNameConstants.HEADER_COLLECTOR_COMPONENT_NAME)
    @Bean
    public FacadeHeaderCollectorConfiguration headerCollectorConfiguration() {
        return new FacadeHeaderCollectorConfiguration();
    }

    @Bean
    public ValuesCollectorFactory headerCollectorFactory() {
        return new SingletonHeaderCollectorsFactory();
    }

    @Bean(name = ComponentNameConstants.HEADER_COLLECTOR_COMPONENT_NAME)
    public Processor kafkaInComponent(FacadeHeaderCollectorConfiguration configuration,
            ValuesCollectorFactory valuesCollectorFactory) {
        return new FacadeHeadersCollector(valuesCollectorFactory, configuration);
    }

}
