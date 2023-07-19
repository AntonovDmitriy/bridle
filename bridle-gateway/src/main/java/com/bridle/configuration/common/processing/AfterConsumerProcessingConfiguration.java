package com.bridle.configuration.common.processing;

import com.bridle.component.collector.FacadeHeaderCollectorConfiguration;
import com.bridle.component.collector.FacadeHeadersCollector;
import com.bridle.component.collector.SingletonHeaderCollectorsFactory;
import com.bridle.component.collector.ValuesCollectorFactory;
import com.bridle.properties.FreemarkerProducerConfiguration;
import com.bridle.properties.JsonSchemaValidatorConfiguration;
import com.bridle.routes.ProcessingParams;
import com.bridle.utils.ComponentCustomizerImpl;
import org.apache.camel.CamelContext;
import org.apache.camel.Processor;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.component.freemarker.FreemarkerComponent;
import org.apache.camel.component.jsonvalidator.JsonValidatorComponent;
import org.apache.camel.model.ConvertBodyDefinition;
import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.model.dataformat.JacksonXMLDataFormat;
import org.apache.camel.model.dataformat.JsonDataFormat;
import org.apache.camel.spi.ComponentCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import static com.bridle.configuration.common.ComponentNameConstants.HEADER_COLLECTOR_COMPONENT_NAME;
import static com.bridle.configuration.common.ComponentNameConstants.VALIDATOR_COMPONENT_NAME;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.freemarker;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.jsonValidator;

public class AfterConsumerProcessingConfiguration {


    public static final String PROCESSING_AFTER_CONSUMER = "processing.after-consumer.";

    public static final String POSTFIX = "AfterConsumer";

    public static final String CONFIGURATION_POSTFIX = "Configuration";

    public static final String JSON_VALIDATOR_AFTER_CONSUMER_CONFIGURATION_PATH =
            PROCESSING_AFTER_CONSUMER + "validation";

    public static final String CONVERT_BODY_CONFIGURATION_PATH = PROCESSING_AFTER_CONSUMER + "convert-body";

    public static final String CONVERT_BODY_NAME = "convertBody" + POSTFIX;

    public static final String HEADER_COLLECTOR_AFTER_CONSUMER_COMPONENT_NAME =
            HEADER_COLLECTOR_COMPONENT_NAME + POSTFIX;

    public static final String HEADER_COLLECTOR_AFTER_CONSUMER_CONFIGURATION =
            HEADER_COLLECTOR_AFTER_CONSUMER_COMPONENT_NAME + CONFIGURATION_POSTFIX;

    public static final String HEADER_COLLECTOR_AFTER_CONSUMER_CONFIGURATION_PATH =
            PROCESSING_AFTER_CONSUMER + "header-collector";

    public static final String UNMARSHALLING_FORMAT_AFTER_CONSUMER_CONFIGURATION_PATH =
            PROCESSING_AFTER_CONSUMER + "unmarshalling-data-format";

    public static final String UNMARSHALLING_FORMAT_AFTER_CONSUMER_NAME = "unmarshallingBody" + POSTFIX;

    public static final String FREEMARKER_AFTER_CONSUMER_CONFIGURATION_PATH = PROCESSING_AFTER_CONSUMER + "transform";

    public static final String MARSHALLING_FORMAT_AFTER_CONSUMER_CONFIGURATION_PATH =
            PROCESSING_AFTER_CONSUMER + "marshalling-data-format";

    public static final String MARSHALLING_FORMAT_AFTER_CONSUMER_NAME = "marshallingBody" + POSTFIX;

    private static final String VALIDATOR_AFTER_CONSUMER_COMPONENT_NAME = VALIDATOR_COMPONENT_NAME + POSTFIX;

    public static final String VALIDATOR_AFTER_CONSUMER_BUILDER = VALIDATOR_AFTER_CONSUMER_COMPONENT_NAME + "Builder";

    public static final String JSON_VALIDATOR_AFTER_CONSUMER_CONFIGURATION =
            VALIDATOR_AFTER_CONSUMER_COMPONENT_NAME + CONFIGURATION_POSTFIX;

    private static final String FREEMARKER_AFTER_CONSUMER_COMPONENT_NAME = "freemarker" + POSTFIX;

    public static final String FREEMARKER_AFTER_CONSUMER_BUILDER = FREEMARKER_AFTER_CONSUMER_COMPONENT_NAME + "Builder";

    public static final String FREEMARKER_AFTER_CONSUMER_CONFIGURATION =
            FREEMARKER_AFTER_CONSUMER_COMPONENT_NAME + CONFIGURATION_POSTFIX;

    // Validation
    @ConditionalOnProperty(value = JSON_VALIDATOR_AFTER_CONSUMER_CONFIGURATION_PATH + ".format",
                           havingValue = "json-schema")
    @ConfigurationProperties(prefix = JSON_VALIDATOR_AFTER_CONSUMER_CONFIGURATION_PATH)
    @Bean(JSON_VALIDATOR_AFTER_CONSUMER_CONFIGURATION)
    public JsonSchemaValidatorConfiguration jsonValidatorAfterConsumerConfiguration() {
        return new JsonSchemaValidatorConfiguration();
    }

    @Bean(name = VALIDATOR_AFTER_CONSUMER_COMPONENT_NAME)
    @ConditionalOnBean(name = JSON_VALIDATOR_AFTER_CONSUMER_CONFIGURATION)
    public JsonValidatorComponent jsonValidatorAfterConsumerComponent() {
        return new JsonValidatorComponent();
    }

    @Bean(VALIDATOR_AFTER_CONSUMER_BUILDER)
    @ConditionalOnBean(name = JSON_VALIDATOR_AFTER_CONSUMER_CONFIGURATION)
    public EndpointProducerBuilder validatorBuilder(@Qualifier(JSON_VALIDATOR_AFTER_CONSUMER_CONFIGURATION)
                                                    JsonSchemaValidatorConfiguration validatorConfiguration) {
        EndpointProducerBuilder result =
                jsonValidator(VALIDATOR_AFTER_CONSUMER_COMPONENT_NAME, validatorConfiguration.getResourceUri());
        validatorConfiguration.getEndpointProperties()
                .ifPresent(additional -> additional.forEach(result::doSetProperty));
        return result;
    }

    @Lazy
    @Bean
    @ConditionalOnBean(name = JSON_VALIDATOR_AFTER_CONSUMER_CONFIGURATION)
    public ComponentCustomizer configureJsonValidatorAfterConsumerComponent(CamelContext context,
                                                                            @Qualifier(
                                                                                    JSON_VALIDATOR_AFTER_CONSUMER_CONFIGURATION)
                                                                            JsonSchemaValidatorConfiguration componentConfiguration) {
        return new ComponentCustomizerImpl(context, componentConfiguration, VALIDATOR_AFTER_CONSUMER_COMPONENT_NAME);
    }

    // convert body

    @ConditionalOnProperty(value = CONVERT_BODY_CONFIGURATION_PATH + ".type")
    @ConfigurationProperties(prefix = CONVERT_BODY_CONFIGURATION_PATH)
    @Bean(CONVERT_BODY_NAME)
    public ConvertBodyDefinition convertBodyAfterConsumer() {
        return new ConvertBodyDefinition();
    }

    // header collector

    @ConfigurationProperties(prefix = HEADER_COLLECTOR_AFTER_CONSUMER_CONFIGURATION_PATH)
    @ConditionalOnProperty(HEADER_COLLECTOR_AFTER_CONSUMER_CONFIGURATION_PATH + ".expression-format")
    @Bean(HEADER_COLLECTOR_AFTER_CONSUMER_CONFIGURATION)
    public FacadeHeaderCollectorConfiguration headerCollectorAfterConsumerConfiguration() {
        return new FacadeHeaderCollectorConfiguration();
    }

    @Bean
    @ConditionalOnMissingBean(value = {ValuesCollectorFactory.class})
    public ValuesCollectorFactory headerCollectorFactory() {
        return new SingletonHeaderCollectorsFactory();
    }

    @Bean(name = HEADER_COLLECTOR_AFTER_CONSUMER_COMPONENT_NAME)
    @ConditionalOnBean(name = HEADER_COLLECTOR_AFTER_CONSUMER_CONFIGURATION)
    public Processor headerCollectorAfterConsumer(
            @Qualifier(HEADER_COLLECTOR_AFTER_CONSUMER_CONFIGURATION) FacadeHeaderCollectorConfiguration configuration,
            ValuesCollectorFactory valuesCollectorFactory) {
        return new FacadeHeadersCollector(valuesCollectorFactory, configuration);
    }


    // format for unmarshalling
    @ConfigurationProperties(prefix = UNMARSHALLING_FORMAT_AFTER_CONSUMER_CONFIGURATION_PATH)
    @ConditionalOnProperty(name = UNMARSHALLING_FORMAT_AFTER_CONSUMER_CONFIGURATION_PATH + ".data-format-name",
                           havingValue = "json")
    @Bean(UNMARSHALLING_FORMAT_AFTER_CONSUMER_NAME)
    public DataFormatDefinition jsonDataFormat() {
        return new JsonDataFormat();
    }

    @ConfigurationProperties(prefix = UNMARSHALLING_FORMAT_AFTER_CONSUMER_CONFIGURATION_PATH)
    @ConditionalOnProperty(name = UNMARSHALLING_FORMAT_AFTER_CONSUMER_CONFIGURATION_PATH + ".data-format-name",
                           havingValue = "xml")
    @Bean(UNMARSHALLING_FORMAT_AFTER_CONSUMER_NAME)
    public DataFormatDefinition xmlDataFormat() {
        return new JacksonXMLDataFormat();
    }

    // transform
    @ConfigurationProperties(prefix = FREEMARKER_AFTER_CONSUMER_CONFIGURATION_PATH)
    @ConditionalOnProperty(value = FREEMARKER_AFTER_CONSUMER_CONFIGURATION_PATH + ".resource-uri")
    @Bean(FREEMARKER_AFTER_CONSUMER_CONFIGURATION)
    public FreemarkerProducerConfiguration freemarkerConfiguration() {
        return new FreemarkerProducerConfiguration();
    }

    @Bean(name = FREEMARKER_AFTER_CONSUMER_COMPONENT_NAME)
    @ConditionalOnBean(name = FREEMARKER_AFTER_CONSUMER_CONFIGURATION)
    public FreemarkerComponent freemarkerComponent() {
        return new FreemarkerComponent();
    }

    @Bean(FREEMARKER_AFTER_CONSUMER_BUILDER)
    @ConditionalOnBean(name = FREEMARKER_AFTER_CONSUMER_CONFIGURATION)
    public EndpointProducerBuilder freemarkerTransformBuilder(
            @Qualifier(FREEMARKER_AFTER_CONSUMER_CONFIGURATION) FreemarkerProducerConfiguration configuration) {
        EndpointProducerBuilder result =
                freemarker(FREEMARKER_AFTER_CONSUMER_COMPONENT_NAME, configuration.getResourceUri());
        configuration.getEndpointProperties().ifPresent(additional -> additional.forEach(result::doSetProperty));
        return result;
    }

    @Lazy
    @Bean
    @ConditionalOnBean(name = FREEMARKER_AFTER_CONSUMER_CONFIGURATION)
    public ComponentCustomizer configureFreemarkerComponent(CamelContext context,
                                                            @Qualifier(FREEMARKER_AFTER_CONSUMER_CONFIGURATION)
                                                            FreemarkerProducerConfiguration componentConfiguration) {
        return new ComponentCustomizerImpl(context, componentConfiguration, FREEMARKER_AFTER_CONSUMER_COMPONENT_NAME);
    }

    // format for marshalling
    @ConfigurationProperties(prefix = MARSHALLING_FORMAT_AFTER_CONSUMER_CONFIGURATION_PATH)
    @ConditionalOnProperty(name = MARSHALLING_FORMAT_AFTER_CONSUMER_CONFIGURATION_PATH + ".data-format-name",
                           havingValue = "json")
    @Bean(MARSHALLING_FORMAT_AFTER_CONSUMER_NAME)
    public DataFormatDefinition marshallingJsonDataFormat() {
        return new JsonDataFormat();
    }

    @ConfigurationProperties(prefix = MARSHALLING_FORMAT_AFTER_CONSUMER_CONFIGURATION_PATH)
    @ConditionalOnProperty(name = MARSHALLING_FORMAT_AFTER_CONSUMER_CONFIGURATION_PATH + ".data-format-name",
                           havingValue = "xml")
    @Bean(MARSHALLING_FORMAT_AFTER_CONSUMER_NAME)
    public DataFormatDefinition marshallingXmlDataFormat() {
        return new JacksonXMLDataFormat();
    }

    @Bean(name = "afterConsumer")
    public ProcessingParams afterConsumerProcessing(
            @Autowired(required = false) @Qualifier(VALIDATOR_AFTER_CONSUMER_BUILDER)
            EndpointProducerBuilder validatorBuilder,
            @Autowired(required = false) @Qualifier(CONVERT_BODY_NAME) ConvertBodyDefinition convertBody,
            @Autowired(required = false) @Qualifier(HEADER_COLLECTOR_AFTER_CONSUMER_COMPONENT_NAME)
            Processor headerCollector,
            @Autowired(required = false) @Qualifier(UNMARSHALLING_FORMAT_AFTER_CONSUMER_NAME)
            DataFormatDefinition unmarshallingFormat,
            @Autowired(required = false) @Qualifier(FREEMARKER_AFTER_CONSUMER_BUILDER)
            EndpointProducerBuilder transform,
            @Autowired(required = false) @Qualifier(MARSHALLING_FORMAT_AFTER_CONSUMER_NAME)
            DataFormatDefinition marshallingFormat) {
        return new ProcessingParams(validatorBuilder,
                                    convertBody,
                                    headerCollector,
                                    unmarshallingFormat,
                                    transform,
                                    marshallingFormat);
    }

}
