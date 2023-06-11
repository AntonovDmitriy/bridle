package com.bridle.core.properties.validation;

import org.springframework.boot.validation.MessageInterpolatorFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Because ConfigurationPropertiesJsr303Validator
 * in org.springframework.boot.context.properties is package private
 */
public class ConfigurationPropertiesJsr303Validator extends LocalValidatorFactoryBean implements Validator {

    public ConfigurationPropertiesJsr303Validator(ApplicationContext applicationContext) {
        setApplicationContext(applicationContext);
        setMessageInterpolator(new MessageInterpolatorFactory().getObject());
        afterPropertiesSet();
    }
}
