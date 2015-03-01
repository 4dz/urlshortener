package com.perry.urlshortener.persistence;


import org.jgroups.JChannel;
import org.jgroups.blocks.ReplicatedHashMap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class LazyReplicatedMap<K,V> implements ConcurrentMap<K,V> {

    private ConcurrentMap<K, V> originalMap;
    private ReplicatedHashMap<K, V> replicatedMap;
    
    public LazyReplicatedMap(ConcurrentMap<K, V> map) { this.originalMap = map; }

    public ReplicatedHashMap<K, V> startReplication(String clusterName, long timeout) throws Exception {
        JChannel channel = new JChannel();
        channel.connect(clusterName);
        this.replicatedMap = new ReplicatedHashMap<>(originalMap, channel);
        replicatedMap.start(timeout);
        return replicatedMap;
    }

    @Override
    public int size() {
        return replicatedMap.size();
    }

    @Override
    public boolean isEmpty() {
        return replicatedMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return replicatedMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return replicatedMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return replicatedMap.get(key);
    }

    @Override
    public V put(K key, V value) {
        return replicatedMap.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return replicatedMap.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        replicatedMap.putAll(m);
    }

    @Override
    public void clear() {
        replicatedMap.clear();
    }

    @Override
    public Set<K> keySet() {
        return replicatedMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return replicatedMap.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return replicatedMap.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return replicatedMap.equals(o);
    }

    @Override
    public int hashCode() {
        return replicatedMap.hashCode();
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return replicatedMap.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return replicatedMap.remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return replicatedMap.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        return replicatedMap.replace(key, value);
    }
}
