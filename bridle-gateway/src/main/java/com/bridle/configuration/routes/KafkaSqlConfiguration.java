package com.bridle.configuration.routes;

import com.bridle.configuration.common.DynamicComponentsComfiguration;
import com.bridle.configuration.common.datasource.DataSourceConfiguration;
import com.bridle.configuration.common.errorhandling.ErrorHandlerConfiguration;
import com.bridle.configuration.common.processing.AfterConsumerProcessingConfiguration;
import com.bridle.routes.ConsumerToProducerRoute;
import com.bridle.routes.model.ConsumerToProducerRouteParams;
import com.bridle.utils.ProcessingParams;
import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ErrorHandlerConfiguration.class, AfterConsumerProcessingConfiguration.class, DataSourceConfiguration.class,
        DynamicComponentsComfiguration.class})
@ConditionalOnProperty(name = "gateway.type",
        havingValue = KafkaSqlConfiguration.GATEWAY_TYPE_KAFKA_SQL)
public class KafkaSqlConfiguration {

    public static final String GATEWAY_TYPE_KAFKA_SQL = "kafka-sql";

    @Bean
    public RouteBuilder kafkaHttpRoute(ErrorHandlerFactory errorHandlerFactory,
            @Qualifier("kafka-in-endpoint")
            EndpointConsumerBuilder kafkaConsumerBuilder,
            @Qualifier("sql-out-endpoint")
            EndpointProducerBuilder sqlCall,
            @Autowired(required = false)
            @Qualifier("afterConsumer")
            ProcessingParams processingAfterConsumerParams) {
        return new ConsumerToProducerRoute(errorHandlerFactory,
                                           new ConsumerToProducerRouteParams(GATEWAY_TYPE_KAFKA_SQL,
                                                                             kafkaConsumerBuilder,
                                                                             processingAfterConsumerParams,
                                                                             sqlCall,
                                                                             null));
    }
}
