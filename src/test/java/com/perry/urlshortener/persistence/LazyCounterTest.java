package com.perry.urlshortener.persistence;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

public class LazyCounterTest {
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
        LazyCounter lazyCounter = new LazyCounter(new StubCounter());
        assertThat(lazyCounter.start("cluster"), instanceOf(org.jgroups.blocks.atomic.Counter.class));
    }

    @Test
    public void shouldReplicateCounter() throws Exception {
        LazyCounter lazyMap1 = new LazyCounter(new StubCounter());
        LazyCounter lazyMap2 = new LazyCounter(new StubCounter());
        lazyMap1.start("cluster");
        lazyMap2.start("cluster");

        long l = lazyMap1.getAndIncrement();
        assertThat(lazyMap2.get(), equalTo(l+1));

        l = lazyMap1.getAndIncrement();
        assertThat(lazyMap2.get(), equalTo(l+1));

        l = lazyMap1.getAndIncrement();
        assertThat(lazyMap2.get(), equalTo(l+1));

//        Thread.sleep(50L);
    }
    
    
    static class StubCounter implements Counter {
        long v;

        @Override
        public long get() {
            return v;
        }

        @Override
        public void set(long value) {
            v=value;
        }

        @Override
        public long getAndIncrement() {
            v+=1;
            return v-1;
        }
    }
    
}