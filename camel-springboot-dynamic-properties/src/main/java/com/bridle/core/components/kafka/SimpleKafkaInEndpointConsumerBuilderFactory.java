package com.bridle.core.components.kafka;

import com.bridle.core.properties.PropertiesLoader;
import org.apache.camel.builder.EndpointConsumerBuilder;

import java.util.Optional;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.kafka;

public class SimpleKafkaInEndpointConsumerBuilderFactory implements KafkaInFactory {

    public static final String DEFAULT_CONFIG_PROPERTIES_KEY_KAFKA_IN = "kafka-in";

    private final PropertiesLoader loader;

    public SimpleKafkaInEndpointConsumerBuilderFactory(PropertiesLoader loader) {
        this.loader = loader;
    }

    @Override
    public EndpointConsumerBuilder create(String componentName) {
        var properties = loader.load(KafkaInProperties.class, componentName);
        var component = kafka(properties.getTopic())
                .brokers(properties.getBrokers())
                .consumersCount(properties.getConsumers())
                .groupId(properties.getGroupId())
                .valueDeserializer(properties.getValueDeserializerClassName())
                .breakOnFirstError(properties.isBreakOnFirstError());

        var heartbeatIntervalMs = properties.getHeartbeatIntervalMs();
        Optional.ofNullable(heartbeatIntervalMs).ifPresent(component::heartbeatIntervalMs);

        var sessionTimeoutMs = properties.getSessionTimeoutMs();
        Optional.ofNullable(sessionTimeoutMs).ifPresent(component::sessionTimeoutMs);

        var maxPollIntervalMs = properties.getMaxPollIntervalMs();
        Optional.ofNullable(maxPollIntervalMs).ifPresent(component::maxPollIntervalMs);

        var maxPollRecords = properties.getMaxPollRecords();
        Optional.ofNullable(maxPollRecords).ifPresent(component::maxPollRecords);

        var maxPartitionFetchBytes = properties.getMaxPartitionFetchBytes();
        Optional.ofNullable(maxPartitionFetchBytes).ifPresent(component::maxPartitionFetchBytes);

        var autoOffsetReset = properties.getAutoOffsetReset();
        Optional.ofNullable(autoOffsetReset).map(AutoOffsetReset::getValue).ifPresent(component::autoOffsetReset);

        var changeOffsetTo = properties.getChangeOffsetTo();
        Optional.ofNullable(changeOffsetTo).map(ChangeOffsetTo::getValue).ifPresent(component::seekTo);

        properties.fillAdditionalProperties(component);
        return component;
    }

    @Override
    public EndpointConsumerBuilder create() {
        return create(DEFAULT_CONFIG_PROPERTIES_KEY_KAFKA_IN);
    }
}
