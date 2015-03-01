package com.perry.urlshortener.persistence;

import org.jgroups.blocks.ReplicatedHashMap;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

public class LazyReplicatedMapTest {
    private final static String IPV4_PREFERENCE_PROPERTY = "java.net.preferIPv4Stack";
    private static String IPSTACK_PREFERENCE_VALUE;
    
    @BeforeClass
    public static void before() {
        IPSTACK_PREFERENCE_VALUE = System.getProperty(IPV4_PREFERENCE_PROPERTY);
        System.setProperty(IPV4_PREFERENCE_PROPERTY,"true");
    }
    
    @AfterClass
    public static void after() {
        if(IPSTACK_PREFERENCE_VALUE ==null) {
            System.clearProperty(IPV4_PREFERENCE_PROPERTY);
        } else {
            System.setProperty(IPV4_PREFERENCE_PROPERTY, IPSTACK_PREFERENCE_VALUE);
        }
    }
    
    @Test
    public void shouldCreateReplicatedHashMap_WhenStartingReplication() throws Exception {
        LazyReplicatedMap<String,String> lazyMap = new LazyReplicatedMap<>(new ConcurrentHashMap<String,String>());
        assertThat(lazyMap.startReplication("cluster", 1000L), instanceOf(ReplicatedHashMap.class));
    }

    @Test
    public void shouldReplicateAdditionsToHashMap() throws Exception {
        LazyReplicatedMap<String,String> lazyMap1 = new LazyReplicatedMap<>(new ConcurrentHashMap<String,String>());
        LazyReplicatedMap<String,String> lazyMap2 = new LazyReplicatedMap<>(new ConcurrentHashMap<String,String>());
        lazyMap1.startReplication("cluster", 1000L);
        lazyMap2.startReplication("cluster", 1000L);
        
        lazyMap1.put("KEY", "VALUE");
        Thread.sleep(50L);
        assertThat(lazyMap2.get("KEY"), equalTo("VALUE"));
    }


}