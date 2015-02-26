package com.perry.urlshortener.lifecycle;

import com.perry.urlshortener.config.Configuration;
import com.perry.urlshortener.persistence.BigOrderedSet;
import com.perry.urlshortener.persistence.MirroredSet;
import com.perry.urlshortener.service.ShortenerService;
import com.perry.urlshortener.util.Utf8String;
import org.jgroups.JChannel;

public interface Scope {
    Configuration getConfiguration();
    JChannel getCluster();
    BigOrderedSet<Utf8String> getDatabase();
    MirroredSet<Utf8String> getMirrorDatabase();
    ShortenerService getShortenerService();
    String getErrorMessage();
    boolean isError();
}
