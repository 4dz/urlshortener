package com.perry.urlshortener.cluster;

import com.perry.urlshortener.lifecycle.MutableScope;
import com.perry.urlshortener.persistence.BigOrderedSet;
import com.perry.urlshortener.persistence.SetModificationListener;
import com.perry.urlshortener.stub.Config;
import com.perry.urlshortener.util.Utf8String;
import org.jgroups.JChannel;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Many of these variables are static because starting the cluster is slow in a test;
 * only want to do it once.
 */
public class ClusterOnStartupTest {
    public static MutableScope scope;
    public static ClusterOnStartup clusterStarter;
    public static String oldIP4Value;
    public static BigOrderedDummySet database;
    
    private static final String IP4Key = "java.net.preferIPv4Stack";
    
    
    @BeforeClass
    public static void init() {
        oldIP4Value = System.getProperty(IP4Key);
        System.setProperty(IP4Key, "false");
        
        scope = new MutableScope(new Config());
        database=new BigOrderedDummySet();
        scope.setDatabase(database);
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

    @Test
    public void shouldAddDatabaseNotifier_WhenStarting() {
        assertThat(database.getSynchronizedListeners().get(0), instanceOf(NotifyClusterOnAddToDatabase.class));
    }
    
    public static class BigOrderedDummySet implements BigOrderedSet<Utf8String> {
        private List<SetModificationListener<Utf8String>> listeners = new ArrayList<>();

        @Override
        public long add(Utf8String element) { return 0; }

        @Override
        public Utf8String get(long i) { return null; }

        @Override
        public Long find(Utf8String element) { return null; }

        @Override
        public void addSynchronizedListener(SetModificationListener<Utf8String> synchronizedListener) {
            this.listeners.add(synchronizedListener);
        }

        @Override
        public void close() {}
        
        public List<SetModificationListener<Utf8String>> getSynchronizedListeners() {
            return listeners;
        }
    }
}