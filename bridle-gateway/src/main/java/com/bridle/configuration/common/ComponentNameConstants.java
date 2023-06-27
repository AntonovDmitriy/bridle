package com.bridle.configuration.common;

public final class ComponentNameConstants {

    public static final String KAFKA_IN_COMPONENT_NAME = "kafka-in";

    public static final String KAFKA_OUT_COMPONENT_NAME = "kafka-out";

    public static final String REST_CALL_COMPONENT_NAME = "rest-call";

    public static final String REST_IN_COMPONENT_NAME = "rest-in";

    public static final String SCHEDULER_COMPONENT_NAME = "main-scheduler";

    public static final String REST_POLL_COMPONENT_NAME = "rest-poll";

    public static final String FREEMARKER_COMPONENT_NAME = "main-freemarker";

    public static final String ERROR_HANDLER_NAME = "error-handler";

    private ComponentNameConstants() {
        throw new IllegalStateException("Utility class");
    }
}
