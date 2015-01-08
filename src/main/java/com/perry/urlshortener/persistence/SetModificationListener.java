package com.perry.urlshortener.persistence;

public interface SetModificationListener<E> {
    void add(long index, E entry);
    void close();
}
