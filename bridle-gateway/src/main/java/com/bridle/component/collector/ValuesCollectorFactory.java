package com.bridle.component.collector;


import com.bridle.component.collector.enums.ExpressionFormat;


public interface ValuesCollectorFactory {

    @SuppressWarnings("rawtypes")
    ValuesCollector createValuesCollector(ExpressionFormat messageFormat);
}
