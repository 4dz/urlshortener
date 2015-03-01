package com.perry.urlshortener.persistence;

import org.jgroups.JChannel;
import org.jgroups.blocks.atomic.CounterService;

public class LazyCounter implements Counter {
    private final Counter local;
    private org.jgroups.blocks.atomic.Counter distributed;
    
    public LazyCounter(Counter local) {
        this.local = local;
    }

    public org.jgroups.blocks.atomic.Counter start(String clusterName) throws Exception {
        JChannel channel = new JChannel();
        CounterService counter_service=new CounterService(channel);
        channel.connect(clusterName);
        distributed=counter_service.getOrCreateCounter("mycounter", local.get());
        return distributed;
    }

    @Override
    public long get() {
        long distributedValue = distributed.get();
        local.set(distributedValue);
        return distributedValue;
    }
    
    @Override
    public long getAndIncrement() {
        long newDistributedValue = distributed.incrementAndGet();
        local.set(newDistributedValue);
        return newDistributedValue-1;
    }

    @Override
    public void set(long value) {
        distributed.set(value);
        local.set(value);
    }


}
