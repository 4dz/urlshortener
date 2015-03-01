package com.perry.urlshortener.persistence;

import org.mapdb.Atomic;
import org.mapdb.BTreeMap;
import org.mapdb.Bind;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Fun;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentMap;

/**
 * Storage backed by http://mapdb.org
 * Uses disk as well as RAM so that the maximum number of entries is a function of disk space.
 * This is slower than a RAM based map for smaller sets.  MapDB claims to be faster for larger
 * sets due to garbage collection overheads.
 */
public class BigOrderedMapDBSet<E> extends AbstractBigOrderedSet<E> {

    private final Map<Long, E> urls;
    private final NavigableSet<Fun.Tuple2<E, Long>> searchIndex;
    private final Counter counter;
    private final DB db;
    
    public BigOrderedMapDBSet(String dbFilename) {
        this(dbFilename, null);
    }
    
    public BigOrderedMapDBSet(String dbFilename, SetModificationListener<E> synchronizedListener) {
        File dbFile = new File(dbFilename);
        db = DBMaker.newFileDB(dbFile)
                .mmapFileEnable()
                .cacheSoftRefEnable()
                .closeOnJvmShutdown()
                .make();
        
        BTreeMap<Long, E> urls = db.getTreeMap("urls");
        this.searchIndex = db.getTreeSet("searchIndex");
        this.counter = createCounter();
        try {
            this.urls = decorate(urls);
        } catch(Exception ex) {
            throw new RuntimeException("Unable to start MapDB", ex);
        }
        
        // bind inverse mapping to primary map, so it is auto-updated
        Bind.mapInverse(urls, searchIndex);

        this.setSynchronizedListener(synchronizedListener);
    }
    
    protected Map<Long, E> decorate(ConcurrentMap<Long, E> urls) throws Exception {
        return urls;
    }
    
    protected Counter createCounter() {
        final Atomic.Long ref = db.getAtomicLong("counter");
        return new Counter() {
            @Override
            public long get() {
                return ref.get();
            }

            @Override
            public long getAndIncrement() {
                return ref.getAndIncrement();
            }
            
            @Override
            public void set(long value) {
                ref.set(value);
            }
        };
    }

    @Override
    public void doClose() {
        db.commit();  //persist changes into disk
        db.close();
    }

    @Override
    public E get(long i) {
        E entry = urls.get(i);
        if(entry==null) {
            throw new NoSuchElementException("[" + i + "] out of bounds [0-" + (counter.get()) +"]");
        }
        return entry;
    }

    @Override
    public Long find(E element) {
        Iterator<Long> it = Fun.filter(searchIndex, element).iterator();
        if(!it.hasNext()) {
            return null;
        }
        return it.next();
    }

    @Override
    public Appender<E> getAppender() {
        return new MapDbSetAppender();
    }

    public class MapDbSetAppender implements Appender<E> {
        private final long id;
        
        public MapDbSetAppender() {
            this.id = counter.getAndIncrement();
        }

        @Override
        public long getIndex() {
            return id;
        }

        @Override
        public long append(E element) {
            urls.put(id, element);
            db.commit();  //persist changes into disk
            return id;
        }

    }

}
