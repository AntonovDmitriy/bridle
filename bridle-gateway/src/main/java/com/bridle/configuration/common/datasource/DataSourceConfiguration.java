package com.bridle.configuration.common.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

public class DataSourceConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "datasources.hikari.main-datasource")
    public HikariConfig mainDataSourceConfig() {
        return new HikariConfig();
    }

    @Bean(name = "mainDataSource")
    public DataSource mainDataSource(HikariConfig hikariConfigFirst, MeterRegistry meterRegistry) {
        HikariDataSource dataSource = new HikariDataSource(hikariConfigFirst);
        dataSource.setMetricRegistry(meterRegistry);
        return dataSource;
    }
}
