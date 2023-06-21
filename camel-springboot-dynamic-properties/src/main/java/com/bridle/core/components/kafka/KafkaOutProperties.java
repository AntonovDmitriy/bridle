package com.bridle.core.components.kafka;

import com.bridle.core.properties.AbstractProperties;

import javax.validation.constraints.NotBlank;

public class KafkaOutProperties extends AbstractProperties {

    @NotBlank
    private String topic;
    @NotBlank
    private String brokers;

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
}
