package com.perry.urlshortener.config;

public interface Configuration {
    public enum Key {
        BASE_URL("/"), DISK_BACKUP_FILEPATH("");
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
