package com.bridle.routes;

import org.apache.camel.component.http.springboot.HttpComponentConfiguration;
import org.apache.camel.component.kafka.springboot.KafkaComponentConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentsProperties {

    private Map<String, KafkaComponentConfiguration> kafka = new HashMap<>();

    private Map<String, HttpComponentConfiguration> http = new HashMap<>();

    public Map<String, KafkaComponentConfiguration> getKafka() {
        return kafka;
    }

    public void setKafka(Map<String, KafkaComponentConfiguration> kafka) {
        this.kafka = kafka;
    }

    public Map<String, HttpComponentConfiguration> getHttp() {
        return http;
    }

    public void setHttp(Map<String, HttpComponentConfiguration> http) {
        this.http = http;
    }
}
