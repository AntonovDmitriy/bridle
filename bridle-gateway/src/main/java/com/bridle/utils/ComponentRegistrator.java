package com.bridle.utils;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.component.http.HttpComponent;
import org.apache.camel.component.kafka.KafkaComponent;
import org.apache.camel.component.sql.SqlComponent;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.http;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.kafka;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.sql;

public class ComponentRegistrator {

    private final ComponentsProperties componentProperties;

    private final Map<String, EndpointProperties> endpointsProperties;

    private final ConfigurableApplicationContext context;

    public ComponentRegistrator(ComponentsProperties componentProperties,
            Map<String, EndpointProperties> endpointsProperties,
            ConfigurableApplicationContext context) {
        this.componentProperties = componentProperties;
        this.endpointsProperties = endpointsProperties;
        this.context = context;
        addComponents();
        addEndpointBuilders();
    }

    private void addEndpointBuilders() {
        var factory = context.getBeanFactory();
        endpointsProperties.forEach(registerEndpointBuilder(factory));
    }

    private BiConsumer<String, EndpointProperties> registerEndpointBuilder(ConfigurableListableBeanFactory factory) {
        return (s, endpointProperties) -> {
            Object endpointBuilder = createEndpointBuilder(context, endpointProperties);
            if (endpointBuilder != null) {
                factory.registerSingleton(s, endpointBuilder);
            }
        };
    }

    private Object createEndpointBuilder(ConfigurableApplicationContext context,
            EndpointProperties endpointProperties) {

        Object result = null;
        Object builder = resolveBuilder(context, endpointProperties);

        if (endpointProperties.isConsumer()) {
            if (builder instanceof EndpointConsumerBuilder consumer) {
                endpointProperties.getAdditional().forEach(consumer::doSetProperty);
                result = consumer;
            }
        } else if (builder instanceof EndpointProducerBuilder producer) {
            endpointProperties.getAdditional().forEach(producer::doSetProperty);
            result = producer;
        }
        return result;
    }

    private Object resolveBuilder(ConfigurableApplicationContext context, EndpointProperties endpointProperties) {
        Object result = null;
        var componentName = endpointProperties.getComponentName();
        Class<?> clazz = context.getBean(componentName).getClass();
        if (clazz == KafkaComponent.class) {
            result = kafka(componentName, (String) endpointProperties.getMandatory().get("topic"));
        } else if (clazz == HttpComponent.class) {
            result = http(componentName,
                          String.format("%s:%s/%s",
                                        endpointProperties.getMandatory().get("host"),
                                        endpointProperties.getMandatory().get("port"),
                                        endpointProperties.getMandatory().get("resource-path")));
        } else if (clazz == SqlComponent.class) {
            result = sql((String) endpointProperties.getMandatory().get("sql-template-uri"));
        }
        return result;
    }

    private void addComponents() {
        var factory = context.getBeanFactory();
        componentProperties
                .getKafka()
                .forEach((componentName, configuration) -> factory.registerSingleton(componentName,
                                                                                     new KafkaComponent()));
        componentProperties
                .getHttp()
                .forEach((componentName, configuration) -> factory.registerSingleton(componentName,
                                                                                     new HttpComponent()));
        componentProperties
                .getSql()
                .forEach((componentName, configuration) -> factory.registerSingleton(componentName,
                                                                                     new SqlComponent()));
    }

    @PostConstruct
    public void init() {
        CamelContext camelContext = context.getBean(CamelContext.class);
        var factory = context.getBeanFactory();
        Stream
                .concat(componentProperties.getKafka().entrySet().stream(),
                        componentProperties.getHttp().entrySet().stream())
                .forEach(entry -> factory.registerSingleton(entry.getKey() + "Customizer",
                                                            new ComponentCustomizerImpl(camelContext,
                                                                                        entry.getValue(),
                                                                                        entry.getKey())));
    }
}
