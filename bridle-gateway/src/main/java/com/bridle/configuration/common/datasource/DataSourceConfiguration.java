package com.bridle.configuration.common.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import javax.sql.DataSource;

public class DataSourceConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "datasources.hikari.main-datasource")
    public HikariConfig mainDataSourceConfig() {
        return new HikariConfig();
    }

    @Bean(name = "mainDataSource")
    @Lazy
    public DataSource mainDataSource(HikariConfig hikariConfigFirst) {
        return new HikariDataSource(hikariConfigFirst);
    }
}
