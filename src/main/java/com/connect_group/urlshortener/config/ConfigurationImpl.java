package com.connect_group.urlshortener.config;

import java.util.Properties;

public enum ConfigurationImpl implements Configuration {
    INSTANCE("/config.properties");

    private final Properties properties;
    
    ConfigurationImpl(String path) {
        properties = new Properties();
        try {
            properties.load(this.getClass().getResourceAsStream(path));
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public String get(Configuration.Key key){
        return properties.getProperty(key.toString(), key.getDefault());
    }


}
