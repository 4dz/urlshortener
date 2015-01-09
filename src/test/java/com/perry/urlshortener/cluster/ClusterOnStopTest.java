package com.perry.urlshortener.cluster;

import com.perry.urlshortener.lifecycle.MutableScope;
import com.perry.urlshortener.stub.Config;
import org.jgroups.JChannel;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ClusterOnStopTest {

    @Test
    public void shouldCloseClusterChannel_WhenStopping() {
        MutableScope scope = new MutableScope(new Config());
        JChannel mockChannel = mock(JChannel.class);
        scope.setCluster(mockChannel);
        ClusterOnStop onStopHandler = new ClusterOnStop();
        
        onStopHandler.onStop(scope);
        
        verify(mockChannel).close();
    }
}