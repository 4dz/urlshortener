package com.perry.urlshortener.persistence;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class BigOrderedReplicatedMapDBSet<E>  extends BigOrderedMapDBSet<E> {

    private LazyReplicatedMap<Long,E> lazyMap;
    private LazyCounter lazyCounter;
    
    public BigOrderedReplicatedMapDBSet(String dbFilename, String clusterName, long timeout) throws Exception {
        super(dbFilename);
        lazyMap.startReplication(clusterName, timeout);
        lazyCounter.start(clusterName);
    }

    @Override
    protected Map<Long, E> decorate(ConcurrentMap<Long, E> urls) throws Exception {
        lazyMap = new LazyReplicatedMap<>(urls);
        return lazyMap;
    }
    
    @Override
    protected Counter createCounter() {
        lazyCounter = new LazyCounter(super.createCounter());
        return lazyCounter;
    }
    
    

}
