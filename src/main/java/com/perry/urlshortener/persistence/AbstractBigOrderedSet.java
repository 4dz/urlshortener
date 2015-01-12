package com.perry.urlshortener.persistence;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBigOrderedSet<E> implements BigOrderedSet<E> {

    private final List<SetModificationListener<E>> synchronizedListeners;
    private boolean closed = false;
    
    public AbstractBigOrderedSet() {
        this.synchronizedListeners = new ArrayList<>();
    }
    
    public void addSynchronizedListener(SetModificationListener<E> synchronizedListener) {
        this.synchronizedListeners.add(synchronizedListener);
    }
    
    @Override
    public final long add(E element) {

        Appender<E> appender;
        
        synchronized(this) {
            Long indexOfElement = find(element);
            if (indexOfElement != null) {
                return indexOfElement;
            } else {
                appender=getAppender();
                
                for(SetModificationListener<E> listener : synchronizedListeners) {
                    listener.add(new SetEntry<>(appender.getIndex(), element));
                }
            }

        }

        return appender.append(element);
    }
    
    @Override
    public final void close() {
        synchronized(this) {
            if(closed) {
                throw new IllegalAccessError("BigOrderedSet is already closed");
            }
            closed = true;
        }
        for(SetModificationListener<E> listener : synchronizedListeners) {
            listener.close();
        }

        doClose();
    }
    
    protected abstract void doClose();
    
    protected abstract Appender<E> getAppender();

    public static interface Appender<E> {
        long getIndex();
        long append(E element);
    }


    protected void finalize() throws Throwable {
        try {
            if(!closed) { close(); }
        } finally {
            super.finalize();
        }
    }
}
