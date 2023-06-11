package com.bridle.core.properties.validation;

import com.bridle.core.components.http.HttpOutProperties;
import com.bridle.core.components.http.SimpleHttpOutEndpointProducerBuilderFactory;
import com.bridle.core.components.scheduler.SchedulerProperties;
import com.bridle.core.components.scheduler.SimpleSchedulerEndpointConsumerBuilderFactory;
import com.bridle.core.configuration.SimpleBridleConfiguration;
import com.bridle.core.properties.PropertiesLoader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = {SimpleBridleConfiguration.class})
@TestPropertySource(properties = {"spring.config.location=classpath:validation/scheduler-application.yml"})
class ConfigurationPropertiesJsr303ValidatorTest {

    private static final int EXPECTED_INTEGER_VALUE = 1;
    private static final String EXPECTED_STRING_VALUE = "localhost:8080";
    @Autowired
    private PropertiesLoader propertiesLoader;

    @Test
    void propertiesLoaderSuccessLoadsPropertiesWithJsr303Validation() {
        SchedulerProperties schedulerProperties = propertiesLoader.load(SchedulerProperties.class,
                SimpleSchedulerEndpointConsumerBuilderFactory.DEFAULT_CONFIG_PROPERTIES_KEY_SCHEDULER);
        assertEquals(EXPECTED_INTEGER_VALUE, schedulerProperties.getThreadCount());
        HttpOutProperties httpOutProperties = propertiesLoader.load(HttpOutProperties.class,
                SimpleHttpOutEndpointProducerBuilderFactory.DEFAULT_CONFIG_PROPERTIES_KEY_HTTP_OUT);
        assertEquals(EXPECTED_STRING_VALUE, httpOutProperties.getUrl());
    }
}