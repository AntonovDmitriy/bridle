package com.bridle.component.collector;


import com.bridle.component.collector.enums.ExpressionFormat;

import java.util.Map;

/**
 * Интерфейс для фабрики сборщика значений.
 * <p>см. {@link ValuesCollector}, {@link ExpressionFormat}.
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
    ValuesCollector createValuesCollector(ExpressionFormat messageFormat,
                                          Map<String, String> queryExpressionsByHeaderName);
}
