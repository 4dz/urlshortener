package com.perry.urlshortener.cluster;

import com.perry.urlshortener.persistence.BigOrderedSet;
import com.perry.urlshortener.persistence.MirroredSet;
import com.perry.urlshortener.persistence.SetModificationListener;
import com.perry.urlshortener.util.Utf8String;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
* Created by adam on 26/02/15.
*/
public class BigOrderedDummySet implements BigOrderedSet<Utf8String>, MirroredSet<Utf8String> {
    private final List<SetModificationListener<Utf8String>> listeners = new ArrayList<>();
    private final Map<Long,Utf8String> map = new HashMap<>();
    private final Map<Utf8String,Long> reverseMap = new HashMap<>();
    private final AtomicLong counter = new AtomicLong();
    
    @Override
    public long add(Utf8String element) {
        Long id = counter.getAndIncrement();
        map.put(id, element);
        reverseMap.put(element, id);
        return id;
    }

    @Override
    public Utf8String get(long i) { return map.get(i); }

    @Override
    public Long find(Utf8String element) { return reverseMap.get(element); }

    @Override
    public void addSynchronizedListener(SetModificationListener<Utf8String> synchronizedListener) {
        this.listeners.add(synchronizedListener);
    }

    @Override
    public void close() {}

    public List<SetModificationListener<Utf8String>> getSynchronizedListeners() {
        return listeners;
    }

    @Override
    public void mirror(Long id, Utf8String value) {
        map.put(id, value);
        reverseMap.put(value, id);
    }
}
