package com.bridle.component.collector;


import com.bridle.component.collector.enums.MessageFormat;

import java.util.Map;

/**
 * Интерфейс для фабрики сборщика значений.
 * <p>см. {@link ValuesCollector}, {@link MessageFormat}.
 * <p>Пример реализации фабрики: {@link SingletonHeaderCollectorsFactory}.
 */
public interface ValuesCollectorFactory {
    /**
     * Создание {@link ValuesCollector}
     *
     * @param messageFormat
     * @param queryExpressionsByHeaderName
     * @return
     */
    @SuppressWarnings("rawtypes")
    ValuesCollector createValuesCollector(MessageFormat messageFormat,
                                          Map<String, String> queryExpressionsByHeaderName);
}
