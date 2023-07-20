package com.bridle.utils;

import org.apache.camel.model.RouteDefinition;
import org.apache.commons.lang3.Validate;

public class ProcessingBuilder {

    private ProcessingBuilder() {
    }

    public static RouteDefinition addProcessing(RouteDefinition route,
            ProcessingParams processingParams,
            String routeId) {
        Validate.notNull(route);
        Validate.notEmpty(routeId);

        route.id(routeId);
        route.process(exchange -> {
        });

        if (processingParams != null) {
            if (processingParams.convertBody() != null) {
                route.addOutput(processingParams.convertBody());
            }

            if (processingParams.validator() != null) {
                route.to(processingParams.validator());
            }

            if (processingParams.headerCollector() != null) {
                route.process(processingParams.headerCollector());
            }

            if (processingParams.beforeTransform() != null) {
                route.unmarshal(processingParams.beforeTransform());
            }

            if (processingParams.transform() != null) {
                route.to(processingParams.transform());
            }

            if (processingParams.afterTransform() != null) {
                route.marshal(processingParams.afterTransform());
            }
        }
        return route;
    }
}
