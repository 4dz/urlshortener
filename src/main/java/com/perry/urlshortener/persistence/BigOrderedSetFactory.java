package com.perry.urlshortener.persistence;

import com.perry.urlshortener.config.Configuration;
import com.perry.urlshortener.util.Utf8String;

public interface BigOrderedSetFactory {
    
    BigOrderedSet<Utf8String> newSet(Configuration config) throws Exception;

}
