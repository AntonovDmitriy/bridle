package com.bridle.component.collector;


import com.bridle.component.collector.enums.MessageFormat;

import java.util.EnumMap;
import java.util.Map;

/**
 * Фабрика для {@link ValuesCollector} по заданному {@link MessageFormat}
 * и соответствия заголовков к выражениям, требуемых для поиска значений.
 */
public class SingletonHeaderCollectorsFactory implements ValuesCollectorFactory {

    @SuppressWarnings("rawtypes")
    private final Map<MessageFormat, ValuesCollector> collectorsByMessageFormat = new EnumMap<>(MessageFormat.class);

    @SuppressWarnings("rawtypes")
    @Override
    public ValuesCollector createValuesCollector(MessageFormat messageFormat,
                                                 Map<String, String> queryExpressionsByHeaderName) {
        checkMessageFormat(messageFormat);
        ValuesCollector result = collectorsByMessageFormat.get(messageFormat);
        if (result == null) {
            synchronized (this) {
                result = collectorsByMessageFormat.get(messageFormat);
                if(result == null) {
                    result = collectorsByMessageFormat.computeIfAbsent(messageFormat,
                            format -> createValuesCollectorForMessageFormat(format, queryExpressionsByHeaderName));
                }
            }
        }
        return result;
    }

    private void checkMessageFormat(MessageFormat messageFormat) {
        if (messageFormat == null) {
            throw new IllegalArgumentException("Error while creating values collector. Message format is null");
        }
    }

    @SuppressWarnings("rawtypes")
    private ValuesCollector createValuesCollectorForMessageFormat(MessageFormat messageFormat,
                                                                  Map<String, String> queryExpressionsByHeaderName) {
        return switch (messageFormat) {
            case XML -> new XpathXmlValuesCollector(queryExpressionsByHeaderName);
            case JSON -> new JsonValuesCollector(queryExpressionsByHeaderName);
            default -> throw new IllegalArgumentException("Unknown format " + messageFormat.name());
        };
    }
}
