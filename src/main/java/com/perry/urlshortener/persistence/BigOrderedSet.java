package com.perry.urlshortener.persistence;

/**
 * Interface for big, ordered set.
 * Possible implementations could be database backed or disk backed.
 */
public interface BigOrderedSet<E> {
    long add(E element);
    E get(long i);
    Long find(E element);
    void addSynchronizedListener(SetModificationListener<E> synchronizedListener);
    void close();
}
