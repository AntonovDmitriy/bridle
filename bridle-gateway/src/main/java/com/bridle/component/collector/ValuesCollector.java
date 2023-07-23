package com.bridle.component.collector;

import java.util.Map;
import java.util.Optional;

/**
 * Интерфейс для сборщика значений из тела сообщения.
 *
 * @param <T> тип тела сообщения.
 */
public interface ValuesCollector<T> {

    /**
     * Сбор значений из заданного тела сообщения.
     *
     * @param body тело сообщения.
     * @return {@link Optional} {@link Map} собранных значений.
     */
    Optional<Map<String, Object>> collectValues(T body, Map<String, String> queryExpressionsByName);
}
