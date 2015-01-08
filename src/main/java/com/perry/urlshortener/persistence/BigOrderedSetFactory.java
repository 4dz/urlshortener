package com.perry.urlshortener.persistence;

import com.perry.urlshortener.config.Configuration;
import com.perry.urlshortener.util.Utf8String;

import java.io.IOException;

public interface BigOrderedSetFactory {
    
    BigOrderedSet<Utf8String> newSet(Configuration config) throws IOException;

}
