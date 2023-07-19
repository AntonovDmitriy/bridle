package com.bridle.component.collector;


import com.bridle.component.collector.enums.ExpressionFormat;

import java.util.EnumMap;
import java.util.Map;

/**
 * Фабрика для {@link ValuesCollector} по заданному {@link ExpressionFormat}
 * и соответствия заголовков к выражениям, требуемых для поиска значений.
 */
public class SingletonHeaderCollectorsFactory implements ValuesCollectorFactory {

    @SuppressWarnings("rawtypes") private final Map<ExpressionFormat, ValuesCollector> collectorsByMessageFormat =
            new EnumMap<>(ExpressionFormat.class);

    @SuppressWarnings("rawtypes")
    @Override
    public ValuesCollector createValuesCollector(ExpressionFormat messageFormat,
            Map<String, String> queryExpressionsByHeaderName) {
        checkMessageFormat(messageFormat);
        ValuesCollector result = collectorsByMessageFormat.get(messageFormat);
        if (result == null) {
            synchronized (this) {
                result = collectorsByMessageFormat.get(messageFormat);
                if (result == null) {
                    result = collectorsByMessageFormat.computeIfAbsent(messageFormat,
                                                                       format -> createValuesCollectorForMessageFormat(
                                                                               format,
                                                                               queryExpressionsByHeaderName));
                }
            }
        }
        return result;
    }

    private void checkMessageFormat(ExpressionFormat expressionFormat) {
        if (expressionFormat == null) {
            throw new IllegalArgumentException("Error while creating values collector. Message format is null");
        }
    }

    @SuppressWarnings("rawtypes")
    private ValuesCollector createValuesCollectorForMessageFormat(ExpressionFormat expressionFormat,
            Map<String, String> queryExpressionsByHeaderName) {
        return switch (expressionFormat) {
            case XPATH -> new XpathXmlValuesCollector(queryExpressionsByHeaderName);
            case JSON -> new JsonValuesCollector(queryExpressionsByHeaderName);
            case JSON_PATH -> new JsonPathValuesCollector(queryExpressionsByHeaderName);
            default -> throw new IllegalArgumentException("Unknown format " + expressionFormat.name());
        };
    }
}
