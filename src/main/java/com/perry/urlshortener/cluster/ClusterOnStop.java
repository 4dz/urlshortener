package com.perry.urlshortener.cluster;

import com.perry.urlshortener.lifecycle.OnStop;
import com.perry.urlshortener.lifecycle.Scope;
import org.jgroups.JChannel;

public class ClusterOnStop implements OnStop {

    @Override
    public void onStop(Scope scope) {
        System.out.println("FINISHED!");
        JChannel channel = scope.getCluster();
        channel.close();
    }
}
