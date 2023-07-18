package com.bridle.component.collector;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Сборщик значений для JSON-формата.
 * <p>Использует JSON-выражения для поиска значений.
 */
public class JsonPathValuesCollector implements ValuesCollector<String> {

    private Map<String, String> queryExpressionsByName = new HashMap<>();

    public JsonPathValuesCollector(Map<String, String> queryExpressionsByName) {
        if (queryExpressionsByName != null) {
            this.queryExpressionsByName = queryExpressionsByName;
        }
    }

    @Override
    public Optional<Map<String, Object>> collectValues(String body) {
        if (body == null) {
            throw new JsonCollectorException("body is null");
        }
        Optional<Map<String, Object>> result = Optional.empty();
        if (!queryExpressionsByName.isEmpty()) {
            Map<String, Object> valuesByName = new HashMap<>();
            try {
                DocumentContext jsonDocument = JsonPath.parse(body);
                for (Map.Entry<String, String> entry : queryExpressionsByName.entrySet()) {
                    Object searchResult = jsonDocument.read(entry.getValue());
                    valuesByName.put(entry.getKey(), searchResult);
                }
            } catch (Exception e) {
                throw new JsonCollectorException("Error during evaluate JSONPath expression: " + e.getMessage(), e);
            }
            result = Optional.of(valuesByName);
        }
        return result;
    }
}
