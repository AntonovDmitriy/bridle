package com.bridle.core.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ContextConfiguration(classes = {SimpleBridleConfiguration.class})
class SimpleBridleConfigurationTest {

    @Test
    void contextStarts() {
        assertTrue(true);
    }
}