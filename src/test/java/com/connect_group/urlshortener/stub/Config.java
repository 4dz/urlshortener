package com.connect_group.urlshortener.stub;

import com.connect_group.urlshortener.config.Configuration;

import java.util.Properties;

public class Config implements Configuration {
    private Properties map = new Properties();
    
    public Config with(String key, String value) {
        map.put(key, value);
        return this;
    }

    @Override
    public String get(Key key) {
        return map.getProperty(key.toString(), key.getDefault());
    }
}
