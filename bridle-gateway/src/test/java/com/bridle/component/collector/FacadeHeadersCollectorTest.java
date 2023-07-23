package com.bridle.component.collector;

import com.bridle.component.collector.enums.ExpressionFormat;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.invocation.Invocation;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacadeHeadersCollectorTest {

    @Spy
    private FacadeHeaderCollectorConfiguration configuration = new FacadeHeaderCollectorConfiguration();

    @Mock
    private ValuesCollectorFactory collectorFactory;

    @Test
    void processPutResultValueCollectorToCamelMessageHeaders() {
        configuration.setExpressionFormat(ExpressionFormat.JSON);
        ValuesCollector<?> mockValuesCollector = mock(ValuesCollector.class);
        when(collectorFactory.createValuesCollector(eq(ExpressionFormat.JSON))).thenReturn(mockValuesCollector);
        Map<String, Object> valuesByName = createValuesByName();
        Exchange mockExchange = mock(Exchange.class);
        Message mockMessage = mock(Message.class);
        when(mockExchange.getIn()).thenReturn(mockMessage);
        when(mockValuesCollector.collectValues(any(), any())).thenReturn(Optional.of(valuesByName));
        FacadeHeadersCollector collector = new FacadeHeadersCollector(collectorFactory, configuration);

        collector.process(mockExchange);

        List<Invocation> invocationsOfSetHeaderMethod = collectAllInvocationsOfSetHeaderMethod(mockMessage);
        Assertions.assertEquals(valuesByName.size(), invocationsOfSetHeaderMethod.size());
        verifyThatAllValuesFromCollectorAnswerInMessageHeaders(valuesByName, invocationsOfSetHeaderMethod);
    }

    private void verifyThatAllValuesFromCollectorAnswerInMessageHeaders(Map<String, Object> valuesByName,
            List<Invocation> invocationsOfSetHeaderMethod) {
        invocationsOfSetHeaderMethod.forEach(invocation -> {
            String headerName = invocation.getArgument(0);
            Object value = invocation.getArgument(1);
            Assertions.assertTrue(valuesByName.containsKey(headerName));
            Assertions.assertEquals(valuesByName.get(headerName), value);
        });
    }

    private List<Invocation> collectAllInvocationsOfSetHeaderMethod(Message mockMessage) {
        return Mockito
                .mockingDetails(mockMessage)
                .getInvocations()
                .stream()
                .filter(invocation -> invocation.getMethod().getName().equals("setHeader"))
                .collect(Collectors.toList());
    }

    private Map<String, Object> createValuesByName() {
        Map<String, Object> valuesByName = new HashMap<>();
        valuesByName.put("RqUID", "RqUIDValue");
        valuesByName.put("SysId", "SysIdValue");
        valuesByName.put("MsgId", "MsgIdValue");
        return valuesByName;
    }

}