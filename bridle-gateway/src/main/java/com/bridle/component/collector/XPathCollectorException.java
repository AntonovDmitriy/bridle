package com.bridle.component.collector;

/**
 * Тип исключения для {@link XpathXmlValuesCollector}.
 */
public class XPathCollectorException extends RuntimeException {

    public XPathCollectorException(String message) {
        super(message);
    }

    public XPathCollectorException(String message, Throwable cause) {
        super(message, cause);
    }
}
