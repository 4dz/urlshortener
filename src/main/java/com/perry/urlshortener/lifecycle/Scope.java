package com.perry.urlshortener.lifecycle;

import com.perry.urlshortener.config.Configuration;
import org.jgroups.JChannel;

public interface Scope {
    Configuration getConfiguration();
    JChannel getCluster();
}
