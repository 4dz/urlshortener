package com.perry.urlshortener.persistence;

import java.io.Serializable;

public class SetEntry<E> implements Serializable {
    private final long id;
    private final E value;
    
    public SetEntry(long id, E value) {
        this.id=id;
        this.value=value;
    }

    public Long getKey() {
        return id;
    }

    public E getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SetEntry setEntry = (SetEntry) o;

        if (id != setEntry.id) return false;
        if (value != null ? !value.equals(setEntry.value) : setEntry.value != null) return false;

        return true;
    }

    @Override
    public String toString() {
        return "SetEntry{" +
                "id=" + id +
                ", value=" + value +
                '}';
    }
}
