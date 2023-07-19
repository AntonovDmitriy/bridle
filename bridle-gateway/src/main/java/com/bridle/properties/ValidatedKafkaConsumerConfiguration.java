package com.bridle.properties;

import org.apache.camel.component.kafka.springboot.KafkaComponentConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.util.Map;
import java.util.Optional;

import static javax.validation.constraints.Pattern.Flag.CASE_INSENSITIVE;

@Validated public class ValidatedKafkaConsumerConfiguration extends KafkaComponentConfiguration {

    private Map<String, Object> endpointProperties;

    @NotEmpty
    private String topic;

    @Override
    @NotEmpty
    public String getBrokers() {
        return super.getBrokers();
    }

    @Override
    @Positive
    public Integer getConsumersCount() {
        return super.getConsumersCount();
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

    @Override
    @NotEmpty
    public String getGroupId() {
        return super.getGroupId();
    }

    @Override
    @Pattern(regexp = "earliest|latest|fail", flags = {CASE_INSENSITIVE})
    public String getAutoOffsetReset() {
        return super.getAutoOffsetReset();
    }

    @Positive
    @Override
    public Long getMaxPollIntervalMs() {
        return super.getMaxPollIntervalMs();
    }

    @Override
    @Positive
    public Integer getMaxPollRecords() {
        return super.getMaxPollRecords();
    }

    @Override
    @Positive
    public Integer getMaxPartitionFetchBytes() {
        return super.getMaxPartitionFetchBytes();
    }

    @Override
    @Positive
    public Integer getHeartbeatIntervalMs() {
        return super.getHeartbeatIntervalMs();
    }

    @Override
    @Positive
    public Integer getSessionTimeoutMs() {
        return super.getSessionTimeoutMs();
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
