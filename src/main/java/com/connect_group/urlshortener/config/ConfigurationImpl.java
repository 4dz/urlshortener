package com.connect_group.urlshortener.config;

import java.util.Properties;

public class ConfigurationImpl implements Configuration {
    private static final Configuration instance;
    
    static {
        instance = load("/config.properties");
    }
    
    private final Properties properties;
    
    private ConfigurationImpl(Properties properties) {
        this.properties = properties;
    }
    
    public static Configuration load(String path) {
        Properties properties = new Properties();
        try {
            properties.load(ConfigurationImpl.class.getResourceAsStream(path));
            return new ConfigurationImpl(properties);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    
    public static Configuration getInstance() {
        return instance;
    }

    @Override
    public String get(Configuration.Key key){
        return properties.getProperty(key.toString(), key.getDefault());
    }

}
