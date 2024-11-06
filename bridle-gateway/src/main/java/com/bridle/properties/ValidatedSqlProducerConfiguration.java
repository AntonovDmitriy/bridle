package com.bridle.properties;

import org.apache.camel.component.sql.springboot.SqlComponentConfiguration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.Map;
import java.util.Optional;

@Validated
public class ValidatedSqlProducerConfiguration extends SqlComponentConfiguration {

    private Map<String, Object> endpointProperties;

    @NotEmpty
    private String query;


    public Optional<Map<String, Object>> getEndpointProperties() {
        return Optional.ofNullable(endpointProperties);
    }

    public void setEndpointProperties(Map<String, Object> endpointProperties) {
        this.endpointProperties = endpointProperties;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
