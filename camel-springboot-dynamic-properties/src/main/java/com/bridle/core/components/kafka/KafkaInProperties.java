package com.bridle.core.components.kafka;

import com.bridle.core.properties.AbstractProperties;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

public class KafkaInProperties extends AbstractProperties {

    @NotBlank
    private String topic;

    @NotBlank
    private String brokers;

    @Min(1)
    private int consumers;

    @NotBlank
    private String groupId;

    private boolean breakOnFirstError;

    private AutoOffsetReset autoOffsetReset;

    @Positive
    private Long maxPollIntervalMs;

    @Positive
    private Integer maxPollRecords;

    @Positive
    private Integer maxPartitionFetchBytes;

    @Positive
    private Integer heartbeatIntervalMs;

    @Positive
    private Integer sessionTimeoutMs;

    private ChangeOffsetTo changeOffsetTo;

    @NotEmpty
    private String valueDeserializerClassName = "org.apache.kafka.common.serialization.StringDeserializer";

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getBrokers() {
        return brokers;
    }

    public void setBrokers(String brokers) {
        this.brokers = brokers;
    }

    public int getConsumers() {
        return consumers;
    }

    public void setConsumers(int consumers) {
        this.consumers = consumers;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public boolean isBreakOnFirstError() {
        return breakOnFirstError;
    }

    public void setBreakOnFirstError(boolean breakOnFirstError) {
        this.breakOnFirstError = breakOnFirstError;
    }

    public AutoOffsetReset getAutoOffsetReset() {
        return autoOffsetReset;
    }

    public void setAutoOffsetReset(AutoOffsetReset autoOffsetReset) {
        this.autoOffsetReset = autoOffsetReset;
    }

    public Long getMaxPollIntervalMs() {
        return maxPollIntervalMs;
    }

    public void setMaxPollIntervalMs(Long maxPollIntervalMs) {
        this.maxPollIntervalMs = maxPollIntervalMs;
    }

    public Integer getMaxPollRecords() {
        return maxPollRecords;
    }

    public void setMaxPollRecords(Integer maxPollRecords) {
        this.maxPollRecords = maxPollRecords;
    }

    public Integer getMaxPartitionFetchBytes() {
        return maxPartitionFetchBytes;
    }

    public void setMaxPartitionFetchBytes(Integer maxPartitionFetchBytes) {
        this.maxPartitionFetchBytes = maxPartitionFetchBytes;
    }

    public Integer getHeartbeatIntervalMs() {
        return heartbeatIntervalMs;
    }

    public void setHeartbeatIntervalMs(Integer heartbeatIntervalMs) {
        this.heartbeatIntervalMs = heartbeatIntervalMs;
    }

    public Integer getSessionTimeoutMs() {
        return sessionTimeoutMs;
    }

    public void setSessionTimeoutMs(Integer sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
    }

    public ChangeOffsetTo getChangeOffsetTo() {
        return changeOffsetTo;
    }

    public void setChangeOffsetTo(ChangeOffsetTo changeOffsetTo) {
        this.changeOffsetTo = changeOffsetTo;
    }

    public String getValueDeserializerClassName() {
        return valueDeserializerClassName;
    }

    public void setValueDeserializerClassName(String valueDeserializerClassName) {
        this.valueDeserializerClassName = valueDeserializerClassName;
    }
}
