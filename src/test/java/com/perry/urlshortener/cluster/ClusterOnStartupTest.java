package com.perry.urlshortener.cluster;

import com.perry.urlshortener.lifecycle.MutableScope;
import com.perry.urlshortener.stub.Config;
import org.jgroups.JChannel;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class ClusterOnStartupTest {
    public static MutableScope scope;
    public static ClusterOnStartup clusterStarter;
    public static String oldIP4Value;
    private static final String IP4Key = "java.net.preferIPv4Stack";
    
    @BeforeClass
    public static void init() {
        oldIP4Value = System.getProperty(IP4Key);
        System.setProperty(IP4Key, "false");
        
        scope = new MutableScope(new Config());
        clusterStarter = new ClusterOnStartup();
        clusterStarter.onStart(scope);
    }
    
    @AfterClass
    public static void finish() {
        if(oldIP4Value==null) {
            System.clearProperty(IP4Key);
        } else {
            System.setProperty(IP4Key, oldIP4Value);
        }
    } 

    @Test
    public void shouldJoinUrlShortenerCluster_WhenStarting() {
        JChannel channel = scope.getCluster();
        assertThat(channel.getClusterName(), equalTo("urlshortener"));
    }

    @Test
    public void shouldAddMessageReceiver_WhenStarting() {
        JChannel channel = scope.getCluster();
        assertThat(channel.getReceiver(), instanceOf(MessageReceiver.class));
    }
    
    @Test
    public void shouldSetPreferIP4SystemPropertyToTrue_WhenStarting() {
        assertThat(System.getProperty(IP4Key), equalTo("true"));
    }

}