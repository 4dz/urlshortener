package com.perry.urlshortener.cluster;

import com.perry.urlshortener.lifecycle.MutableScope;
import com.perry.urlshortener.lifecycle.OnStartup;
import org.jgroups.JChannel;
import org.jgroups.ReceiverAdapter;

public class ClusterOnStartup implements OnStartup {
    public static final String CHANNEL_NAME = "urlshortener";
    @Override
    public void onStart(MutableScope scope) {
        // TODO this should be controlled by a flag and/or set in the java command line
        // which starts tomcat.
        System.setProperty("java.net.preferIPv4Stack","true");
        ReceiverAdapter receiver = createReceiver();
        try {

            JChannel channel = new JChannel();
            channel.setReceiver(receiver);
            channel.connect(CHANNEL_NAME);

            scope.setCluster(channel);
            
            if(scope.getDatabase()!=null) {
                scope.getDatabase().addSynchronizedListener(new NotifyClusterOnAddToDatabase(channel));
            }
        } catch (Exception e) {
            // TODO!
            e.printStackTrace();
        }
    }

    protected ReceiverAdapter createReceiver() {
        return new MessageReceiver();
    }
}
