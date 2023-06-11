package com.bridle.core.configuration;

import com.bridle.core.properties.DefaultPropertiesLoaderWIthJsr303Validation;
import com.bridle.core.properties.PropertiesLoader;
import com.bridle.core.properties.validation.ConfigurationPropertiesJsr303Validator;
import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.validation.ValidationBindHandler;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimpleBridleConfiguration {

    private final ApplicationContext context;

    public SimpleBridleConfiguration(ApplicationContext context) {
        this.context = context;
    }

    @Bean
    PropertiesLoader propertiesLoaderService() {
        Binder configurationBinder = new Binder(
                ConfigurationPropertySources.get(context.getEnvironment()));
        BindHandler validationHandler = new ValidationBindHandler(
                new ConfigurationPropertiesJsr303Validator(context));
        return new DefaultPropertiesLoaderWIthJsr303Validation(configurationBinder,
                validationHandler);
    }
}
