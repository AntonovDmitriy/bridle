package com.bridle.component.collector;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Конфигурируемый сборщик значений в заголовки для {@link Exchange} с настраиваемым типом формата сообщения.
 * <p>см. {@link ValuesCollectorFactory}, {@link ValuesCollector}, {@link org.springframework.validation.MessageCodeFormatter}.
 */
public class FacadeHeadersCollector implements Processor {

    private final ValuesCollectorFactory collectorFactory;

    private final FacadeHeaderCollectorConfiguration configuration;

    @SuppressWarnings("rawtypes")
    private ValuesCollector collector;

    public FacadeHeadersCollector(ValuesCollectorFactory collectorFactory,
                                  FacadeHeaderCollectorConfiguration configuration) {
        this.collectorFactory = collectorFactory;
        this.configuration = configuration;

        initValuesCollectorForMessageFormat();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(Exchange exchange) {
        if (collector != null) {
            collector.collectValues(exchange.getIn().getBody(String.class))
                    .ifPresent(putValuesToExchangeHeaders(exchange));
        }
    }

    private void initValuesCollectorForMessageFormat() {
        if (configuration.getExpressionFormat() != null) {
            collector = collectorFactory.createValuesCollector(configuration.getExpressionFormat(),
                                                               configuration.getQueryExpressionsByHeaderName());
        }
    }

    private Consumer<Map<String, Object>> putValuesToExchangeHeaders(Exchange exchange) {
        Message message = exchange.getIn();
        return valuesFromMessageBody -> valuesFromMessageBody.forEach(message::setHeader);
    }
}
