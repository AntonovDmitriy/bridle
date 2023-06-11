package com.bridle.core.properties;

import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;

public class DefaultPropertiesLoaderWIthJsr303Validation implements PropertiesLoader {

    private Binder configurationBinder;
    private BindHandler bindHandler;

    public DefaultPropertiesLoaderWIthJsr303Validation(Binder configurationBinder, BindHandler bindHandler) {
        this.configurationBinder = configurationBinder;
        this.bindHandler = bindHandler;
    }

    @Override
    public <T> T load(Class<T> clazz, String prefix) {
        return configurationBinder.bind(prefix, Bindable.of(clazz), bindHandler).get();
    }
}
