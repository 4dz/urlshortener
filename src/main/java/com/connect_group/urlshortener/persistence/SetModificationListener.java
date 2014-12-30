package com.connect_group.urlshortener.persistence;

public interface SetModificationListener<E> {
    void add(long index, E entry);
}
