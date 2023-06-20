package com.bridle.core.components.scheduler;

import com.bridle.core.properties.PropertiesLoader;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimpleSchedulerEndpointConsumerBuilderFactoryTest {

    private static final int EXPECTED_DELAY = 12344;

    private static final int EXPECTED_THREAD_COUNT = 34;

    @InjectMocks
    private SimpleSchedulerEndpointConsumerBuilderFactory schedulerFactory;

    @Mock
    private PropertiesLoader loader;

    private SchedulerProperties properties;

    @BeforeEach
    public void init() {
        initSchedulerProperties();
    }

    private void initSchedulerProperties() {
        properties = new SchedulerProperties();
        properties.setDelayMillis(EXPECTED_DELAY);
        properties.setThreadCount(EXPECTED_THREAD_COUNT);
    }

    @Test
    void createReturnsProducerBuilderWithLoadedPropertiesForDefaultComponentName() {
        when(loader.load(SchedulerProperties.class,
                SimpleSchedulerEndpointConsumerBuilderFactory.DEFAULT_CONFIG_PROPERTIES_KEY_SCHEDULER))
                .thenReturn(properties);

        EndpointConsumerBuilder schedulerBuilder = schedulerFactory.create();

        verifyThatAllLoadedPropertiesAreSetToBuilder(schedulerBuilder);
    }

    @Test
    void createReturnsProducerBuilderWithLoadedPropertiesForComponentName() {
        String componentName = "custom-scheduler";
        when(loader.load(SchedulerProperties.class, componentName)).thenReturn(properties);

        EndpointConsumerBuilder schedulerBuilder = schedulerFactory.create(componentName);

        verifyThatAllLoadedPropertiesAreSetToBuilder(schedulerBuilder);
    }

    private void verifyThatAllLoadedPropertiesAreSetToBuilder(EndpointConsumerBuilder schedulerBuilder) {
        String uri = schedulerBuilder.getUri();
        verifyThatUriContainsProperty(uri, "delay", EXPECTED_DELAY);
        verifyThatUriContainsProperty(uri, "poolSize", EXPECTED_THREAD_COUNT);
    }

    private void verifyThatUriContainsProperty(String uri, String property, Object value) {
        assertTrue(() -> uri.contains(property + "=" + value));
    }

}