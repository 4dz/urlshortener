package com.perry.urlshortener.config;

public interface Configuration {
    public enum Key {
        BASE_URL("/"), DISK_BACKUP_FILEPATH(""), DATABASE_FACTORY_CLASSPATH("com.perry.urlshortener.persistence.BigOrderedRAMSetFactory");
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
