package com.perry.urlshortener.stub;

import com.perry.urlshortener.config.Configuration;

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
