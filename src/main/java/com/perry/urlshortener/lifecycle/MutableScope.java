package com.perry.urlshortener.lifecycle;

import com.perry.urlshortener.config.Configuration;
import org.jgroups.JChannel;

public class MutableScope implements Scope {
    private final Configuration configuration;
    private JChannel channel;
    
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
    
    public void setCluster(JChannel channel) {
        this.channel = channel;
    }
}
