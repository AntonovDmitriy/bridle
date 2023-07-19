package com.bridle.component.collector;

/**
 * Тип исключения для {@link JsonValuesCollector}.
 */
public class JsonCollectorException extends RuntimeException {

    public JsonCollectorException(String message) {
        super(message);
    }

    public JsonCollectorException(String message,
            Throwable cause) {
        super(message, cause);
    }
}
