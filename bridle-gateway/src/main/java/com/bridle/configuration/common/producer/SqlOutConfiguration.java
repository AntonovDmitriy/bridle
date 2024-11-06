package com.bridle.configuration.common.producer;

import com.bridle.properties.ValidatedKafkaProducerConfiguration;
import com.bridle.properties.ValidatedSqlProducerConfiguration;
import com.bridle.utils.ComponentCustomizerImpl;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.apache.camel.component.sql.SqlComponent;
import org.apache.camel.spi.ComponentCustomizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import static com.bridle.configuration.common.ComponentNameConstants.KAFKA_OUT_COMPONENT_NAME;
import static com.bridle.configuration.common.ComponentNameConstants.SQL_OUT_COMPONENT_NAME;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.kafka;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.sql;

public class SqlOutConfiguration {

    @ConfigurationProperties(prefix = SQL_OUT_COMPONENT_NAME)
    @Bean
    public ValidatedSqlProducerConfiguration sqlConfiguration() {
        return new ValidatedSqlProducerConfiguration();
    }

    @Bean(name = SQL_OUT_COMPONENT_NAME)
    public SqlComponent sqlOutComponent() {
        return new SqlComponent();
    }

    @Lazy
    @Bean
    public ComponentCustomizer configureSqlOutComponent(CamelContext context,
                                                        ValidatedSqlProducerConfiguration componentConfiguration) {
        return new ComponentCustomizerImpl(context, componentConfiguration, SQL_OUT_COMPONENT_NAME);
    }

    @Bean
    public EndpointProducerBuilder sqlProducerBuilder(ValidatedSqlProducerConfiguration configuration) {
        EndpointProducerBuilder result = sql(SQL_OUT_COMPONENT_NAME, configuration.getQuery());
        configuration.getEndpointProperties().ifPresent(additional -> additional.forEach(result::doSetProperty));
        return result;
    }
}
