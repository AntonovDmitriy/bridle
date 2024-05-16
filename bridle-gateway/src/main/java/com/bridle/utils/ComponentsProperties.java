package com.bridle.utils;

import org.apache.camel.component.http.springboot.HttpComponentConfiguration;
import org.apache.camel.component.kafka.springboot.KafkaComponentConfiguration;
import org.apache.camel.component.sql.springboot.SqlComponentConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ComponentsProperties {

    private Map<String, KafkaComponentConfiguration> kafka = new HashMap<>();

    private Map<String, HttpComponentConfiguration> http = new HashMap<>();

    private Map<String, SqlComponentConfiguration> sql = new HashMap<>();

    private Map<String, SqlComponentConfiguration> procedure = new HashMap<>();

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

    public Map<String, SqlComponentConfiguration> getSql() {
        return sql;
    }

    public void setSql(Map<String, SqlComponentConfiguration> sql) {
        this.sql = sql;
    }

    public Map<String, SqlComponentConfiguration> getProcedure() {
        return procedure;
    }

    public void setProcedure(Map<String, SqlComponentConfiguration> procedure) {
        this.procedure = procedure;
    }
}
