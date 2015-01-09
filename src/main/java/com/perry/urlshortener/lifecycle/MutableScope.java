package com.perry.urlshortener.lifecycle;

import com.perry.urlshortener.config.Configuration;
import com.perry.urlshortener.persistence.BigOrderedSet;
import com.perry.urlshortener.util.Utf8String;
import org.jgroups.JChannel;

public class MutableScope implements Scope {
    private final Configuration configuration;
    private JChannel channel;
    private BigOrderedSet<Utf8String> database;
    private String error;
    
    public MutableScope(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public JChannel getCluster() {
        return channel;
    }

    @Override
    public BigOrderedSet<Utf8String> getDatabase() {
        return database;
    }

    @Override
    public String getErrorMessage() {
        return error;
    }

    @Override
    public boolean isError() {
        return error!=null;
    }

    public void setCluster(JChannel channel) {
        this.channel = channel;
    }

    public void setDatabase(BigOrderedSet<Utf8String> database) {
        this.database=database;
    }

    public void setError(String message) {
        this.error = message;
    }
}
