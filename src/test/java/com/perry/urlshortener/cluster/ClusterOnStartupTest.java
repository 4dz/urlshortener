package com.perry.urlshortener.cluster;

import com.perry.urlshortener.lifecycle.MutableScope;
import com.perry.urlshortener.persistence.MirroredSet;
import com.perry.urlshortener.persistence.SetEntry;
import com.perry.urlshortener.stub.Config;
import com.perry.urlshortener.util.Utf8String;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

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
        assertThat(channel.getClusterName(), equalTo(ClusterOnStartup.CHANNEL_NAME));
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
    
    @Test
    public void shouldReceiveMessages_WhenBroadcast() throws Exception {
        
        final SetEntry[] receiveBuffer = startReceiver();
        SetEntry transmitted = new SetEntry<>(999, new Utf8String("Expected Message"));
        transmit(transmitted);
        SetEntry received = waitForMessage(receiveBuffer);

        assertThat(received, equalTo(transmitted));
    }

    
    private SetEntry waitForMessage(final SetEntry[] receiveBuffer) throws InterruptedException {
        synchronized (receiveBuffer) { receiveBuffer.wait(1000); }

        return receiveBuffer[0];
    }

    private SetEntry[] startReceiver() {
        @SuppressWarnings(value = "unchecked")
        final SetEntry[] receiveBuffer = new SetEntry[] {null};
        ClusterOnStartup clusterStarter = new ClusterOnStartup() {
            @Override
            protected ReceiverAdapter createReceiver(MirroredSet<Utf8String> database) {
                return new ReceiverAdapter() {
                    @Override
                    public void receive(Message msg) {
                        super.receive(msg);
                        synchronized (receiveBuffer) {
                            Object o = msg.getObject();
                            if(o instanceof SetEntry) {
                                receiveBuffer[0] = (SetEntry) o;
                            }
                            receiveBuffer.notifyAll();
                        }
                    }
                };
            }
        };
        clusterStarter.onStart(new MutableScope(new Config()));
        
        return receiveBuffer;
    }

    private void transmit(SetEntry transmitted) throws Exception {
        JChannel broadcastChannel = new JChannel();
        broadcastChannel.connect(ClusterOnStartup.CHANNEL_NAME);
        broadcastChannel.send(new Message(null,null,transmitted));
    }

}