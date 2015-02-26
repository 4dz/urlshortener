package com.perry.urlshortener.persistence;

public interface MirroredSet<E> {
    void mirror(Long id, E value);
}
