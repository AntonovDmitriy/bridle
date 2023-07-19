package com.bridle.properties;

import org.apache.camel.component.kafka.springboot.KafkaComponentConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import java.util.Map;
import java.util.Optional;

@Validated public class ValidatedKafkaProducerConfiguration extends KafkaComponentConfiguration {

    private Map<String, Object> endpointProperties;

    @NotEmpty
    private String topic;

    @Override
    @NotEmpty
    public String getBrokers() {
        return super.getBrokers();
    }

    @AssertTrue(message = "ssl configuration is not consistent")
    public boolean isSslSettingsConsistent() {
        boolean result = true;
        if (getSecurityProtocol() == null || (!getSecurityProtocol().equals("PLAINTEXT") && StringUtils.isAnyBlank(
                getSslKeystoreLocation(),
                getSslKeystorePassword(),
                getSslTruststoreLocation(),
                getSslTruststorePassword()))) {
            result = false;
        }
        return result;
    }

    @AssertTrue(message = "if retries is not null then use positive number")
    public boolean isRetriesConsistent() {
        boolean result = true;
        Integer retries = getRetries();
        if (retries != null && retries <= 0) {
            result = false;
        }
        return result;
    }

    public Optional<Map<String, Object>> getEndpointProperties() {
        return Optional.ofNullable(endpointProperties);
    }

    public void setEndpointProperties(Map<String, Object> endpointProperties) {
        this.endpointProperties = endpointProperties;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
