package com.bridle.configuration.common;

import com.bridle.utils.ComponentRegistrator;
import com.bridle.utils.ComponentsProperties;
import com.bridle.utils.EndpointProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

public class DynamicComponentsComfiguration {

    @ConfigurationProperties(prefix = "components")
    @Bean
    public ComponentsProperties componentsProperties() {
        return new ComponentsProperties();
    }

    @ConfigurationProperties(prefix = "endpoints")
    @Bean
    public Map<String, EndpointProperties> endpointsProperties() {
        return new HashMap<>();
    }

    @Bean
    public ComponentRegistrator componentRegistrator(ComponentsProperties componentsProperties,
            Map<String, EndpointProperties> endpointsProperties,
            ConfigurableApplicationContext context) {
        return new ComponentRegistrator(componentsProperties, endpointsProperties, context);
    }
}
