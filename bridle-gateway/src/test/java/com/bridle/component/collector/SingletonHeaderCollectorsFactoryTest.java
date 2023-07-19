package com.bridle.component.collector;


import com.bridle.component.collector.enums.ExpressionFormat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static com.bridle.component.collector.CollectorTestUtils.createCorrectJsonExpressionsByName;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class) class SingletonHeaderCollectorsFactoryTest {

    @Test
    void createValuesCollectorThrowsExceptionWhenExpressionFormatArgumentIsNull() {
        SingletonHeaderCollectorsFactory factory = new SingletonHeaderCollectorsFactory();
        Map<String, String> expressions = createCorrectJsonExpressionsByName();

        assertThrows(IllegalArgumentException.class, () -> factory.createValuesCollector(null, expressions));
    }

    @Test
    void createValuesCollectorReturnsTheSameValuesCollectorForExpressionFormat() {
        ValuesCollectorFactory collectorFactory = new SingletonHeaderCollectorsFactory();

        ValuesCollector<?> jsonCollectorFromFirstInvocation =
                collectorFactory.createValuesCollector(ExpressionFormat.JSON, createCorrectJsonExpressionsByName());
        ValuesCollector<?> jsonCollectorFromSecondInvocation =
                collectorFactory.createValuesCollector(ExpressionFormat.JSON, createCorrectJsonExpressionsByName());
        ValuesCollector<?> xmlCollectorFromFirstInvocation =
                collectorFactory.createValuesCollector(ExpressionFormat.XPATH, createCorrectJsonExpressionsByName());
        ValuesCollector<?> xmlCollectorFromSecondInvocation =
                collectorFactory.createValuesCollector(ExpressionFormat.XPATH, createCorrectJsonExpressionsByName());

        assertSame(jsonCollectorFromFirstInvocation, jsonCollectorFromSecondInvocation);
        assertSame(xmlCollectorFromFirstInvocation, xmlCollectorFromSecondInvocation);
    }
}