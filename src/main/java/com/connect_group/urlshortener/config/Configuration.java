package com.connect_group.urlshortener.config;

public interface Configuration {
    public enum Key {
        BASE_URL("/");

        private final String defaultValue;

        Key(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public String getDefault() {
            return defaultValue;
        }
    }
    
    String get(Key key);
}
