package com.bridle.component.collector;

import com.bridle.component.collector.enums.MessageFormat;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Validated
public class FacadeHeaderCollectorConfiguration {

    private Map<String, String> queryExpressionsByHeaderName;
    private MessageFormat messageFormat;

    public Map<String, String> getQueryExpressionsByHeaderName() {
        return queryExpressionsByHeaderName;
    }

    public void setQueryExpressionsByHeaderName(Map<String, String> queryExpressionsByHeaderName) {
        this.queryExpressionsByHeaderName = queryExpressionsByHeaderName;
    }

    public MessageFormat getMessageFormat() {
        return messageFormat;
    }

    public void setMessageFormat(MessageFormat messageFormat) {
        this.messageFormat = messageFormat;
    }
}
