package com.perry.urlshortener.persistence;

public interface SetModificationListener<E> {
    void add(SetEntry<E> entry);
    void close();
}
