package com.bridle.configuration.common;

public final class ComponentNameConstants {

    public static final String KAFKA_IN_COMPONENT_NAME = "kafka-in";

    public static final String KAFKA_OUT_COMPONENT_NAME = "kafka-out";

    public static final String REST_CALL_COMPONENT_NAME = "rest-call";

    public static final String REST_IN_COMPONENT_NAME = "rest-in";

    public static final String SCHEDULER_COMPONENT_NAME = "main-scheduler";

    public static final String REST_POLL_COMPONENT_NAME = "rest-poll";

    public static final String FREEMARKER_COMPONENT_NAME = "main-freemarker";

    public static final String ERROR_RESPONSE_FREEMARKER_COMPONENT_NAME = "error-response-freemarker";

    public static final String VALIDATION_RESPONSE_FREEMARKER_COMPONENT_NAME = "validation-error-response-freemarker";

    public static final String SUCCESS_RESPONSE_FREEMARKER_COMPONENT_NAME = "success-response-freemarker";

    public static final String HEADER_COLLECTOR_COMPONENT_NAME = "header-collector";

    public static final String VALIDATOR_COMPONENT_NAME = "validator";

    public static final String ERROR_HANDLER_NAME = "error-handler";

    public static final String REDELIVERY_POLICY = "redelivery-policy";

    private ComponentNameConstants() {
        throw new IllegalStateException("Utility class");
    }
}
