package com.bridle.core.properties;

public interface PropertiesLoader {

    <T> T load(Class<T> clazz, String prefix);
}
