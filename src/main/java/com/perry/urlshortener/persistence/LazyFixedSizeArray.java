package com.perry.urlshortener.persistence;

public class LazyFixedSizeArray {
    private final int size;
    private Object[] array = null;
    
    public LazyFixedSizeArray(int size) {
        this.size = size;
    }
    
    public Object get(int index) {
        ensureArrayExists();
        return array[index];
    }
    
    public void set(int index, Object value) {
        ensureArrayExists();
        array[index] = value;
    }

    private void ensureArrayExists() {
        if(array==null) {
            synchronized (this) {
                if(array==null) array = new Object[size];
            }
        }
    }
}
