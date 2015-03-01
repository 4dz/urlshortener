package com.perry.urlshortener.persistence;

public interface Counter {
    
    long get();
    void set(long value);
    long getAndIncrement();
}
