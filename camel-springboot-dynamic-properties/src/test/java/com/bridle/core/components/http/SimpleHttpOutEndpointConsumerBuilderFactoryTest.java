package com.bridle.core.components.http;

import com.bridle.core.properties.PropertiesLoader;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimpleHttpOutEndpointConsumerBuilderFactoryTest {

    @InjectMocks
    private SimpleHttpOutEndpointProducerBuilderFactory httpOutFactory;
    @Mock
    private PropertiesLoader loader;
    private HttpOutProperties properties;

    @BeforeEach
    public void init() {
        initHttpOutProperties();
    }

    private void initHttpOutProperties() {
        properties = new HttpOutProperties();
        properties.setUrl("localhost:8093");
        properties.setAdditional(Map.of("propKey", "myPropValue"));
        properties.setAdditional(Map.of("myPropKey", "myPropValueSecond"));
    }

    @Test
    void createReturnsProducerBuilderWithLoadedPropertiesForDefaultComponentName() {
        when(loader.load(HttpOutProperties.class,
                SimpleHttpOutEndpointProducerBuilderFactory.DEFAULT_CONFIG_PROPERTIES_KEY_HTTP_OUT))
                .thenReturn(properties);

        EndpointProducerBuilder httpOutProducerBuilder = httpOutFactory.create();

        String uri = httpOutProducerBuilder.getUri();
        verifyThatUriContainsAllAdditionalProperties(uri, properties);
        verifyThatUriContainsHostNameFromProperties(uri);
    }

    @Test
    void createReturnsProducerBuilderWithLoadedPropertiesForComponentName() {
        String componentName = "http-out";
        when(loader.load(HttpOutProperties.class, componentName)).thenReturn(properties);

        EndpointProducerBuilder httpOutProducerBuilder = httpOutFactory.create(componentName);

        String uri = httpOutProducerBuilder.getUri();
        verifyThatUriContainsAllAdditionalProperties(uri, properties);
        verifyThatUriContainsHostNameFromProperties(uri);
    }

    private void verifyThatUriContainsHostNameFromProperties(String uri) {
        assertTrue(() -> uri.startsWith("http://" + properties.getUrl()));
    }

    private void verifyThatUriContainsAllAdditionalProperties(String uri, HttpOutProperties properties) {
        assertAll(properties.getAdditional()
                .entrySet()
                .stream()
                .map(entry -> () -> uri.contains(entry.getKey() + "=" + entry.getValue()))
        );
    }
}