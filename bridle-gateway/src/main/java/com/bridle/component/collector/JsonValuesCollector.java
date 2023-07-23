package com.bridle.component.collector;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Сборщик значений для JSON-формата.
 * <p>Использует JSON-выражения для поиска значений.
 */
public class JsonValuesCollector implements ValuesCollector<String> {

    @Override
    public Optional<Map<String, Object>> collectValues(String body, Map<String, String> queryExpressionsByName) {
        if (body == null) {
            throw new JsonCollectorException("body is null");
        }
        Optional<Map<String, Object>> result = Optional.empty();
        if (!queryExpressionsByName.isEmpty()) {
            Map<String, Object> valuesByName = new HashMap<>();
            try {
                JsonNode jsonDocument = new ObjectMapper().readTree(body);
                for (Map.Entry<String, String> entry : queryExpressionsByName.entrySet()) {
                    JsonNode searchResult = jsonDocument.at(entry.getValue());
                    String textSearchResult = !searchResult.isMissingNode() ? searchResult.asText() : null;
                    valuesByName.put(entry.getKey(), textSearchResult);
                }
            } catch (Exception e) {
                throw new JsonCollectorException("Error during evaluate JSONPath expression: " + e.getMessage(), e);
            }
            result = Optional.of(valuesByName);
        }
        return result;
    }
}
