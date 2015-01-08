package com.perry.urlshortener.persistence;

public abstract class AbstractBigOrderedSet<E> implements BigOrderedSet<E> {

    private SetModificationListener<E> synchronizedListener;
    private boolean closed = false;
    
    public AbstractBigOrderedSet() {}
    
    public void setSynchronizedListener(SetModificationListener<E> synchronizedListener) {
        this.synchronizedListener = synchronizedListener;
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

                if(synchronizedListener !=null) {
                    synchronizedListener.add(appender.getIndex(), element);
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
        if(synchronizedListener !=null) {
            synchronizedListener.close();
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
