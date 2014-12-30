package com.connect_group.urlshortener.util;

public interface SetModificationListener<E> {
    void add(long index, E entry);
}
